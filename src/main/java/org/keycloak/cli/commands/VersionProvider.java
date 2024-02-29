package org.keycloak.cli.commands;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import picocli.CommandLine;

public class VersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() {
        Config cf = ConfigProvider.getConfig(getClass().getClassLoader());
        return new String[] {
                cf.getValue("quarkus.application.version", String.class)
        };
    }
}
