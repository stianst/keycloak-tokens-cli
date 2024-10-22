package org.keycloak.cli.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class FilePermissionUtils {

    public static void userOnlyPermissions(File file) {
        try {
            try {
                Files.setPosixFilePermissions(file.toPath(), Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
            } catch (UnsupportedOperationException e) {
                file.setReadable(true, true);
                file.setWritable(true, true);
            }
        } catch (IOException e) {
            throw new ConfigException("Failed to set file permissions");
        }
    }

}
