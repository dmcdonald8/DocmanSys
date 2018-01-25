package main.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

/* File I/O service for storing, loading and deleting files in the filesystem */
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, String branch) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            /* Create directory branch */
            File destDir = new File (this.rootLocation.toString() + branch);
            /* Set file destination */
            Path destPath = Paths.get(destDir.getPath());
            /* copy file */
            Files.copy(file.getInputStream(), destPath.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return walk(this.rootLocation, 6)
                    .filter(path -> !path.equals(this.rootLocation) && !path.toFile().isDirectory())
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    public Path load(String filepath) {
        System.out.println(filepath);
        Path path = rootLocation.resolve(Paths.get(filepath));
        final File directory = path.toFile();
        System.out.println(directory.listFiles()[0].toString());
        return directory.listFiles()[0].toPath();
    }

    @Override
    public Resource loadAsResource(String filepath) {
        try {
            System.out.println("LoadAsResource firing" + filepath);
            Path file = load(filepath);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filepath);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filepath, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void deleteBranch(String username) {
        Path branchPath = Paths.get(rootLocation.toString(), username);
        FileSystemUtils.deleteRecursively(branchPath.toFile());
    }

    @Override
    public void init() {
        try {
            if (!Files.isDirectory(rootLocation)) Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
    @Override
    public Path getRootLocation(){
        return this.rootLocation;
    }
}
