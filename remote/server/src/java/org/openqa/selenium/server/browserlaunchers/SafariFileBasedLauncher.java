package org.openqa.selenium.server.browserlaunchers;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.internal.FileHandler;
import org.openqa.selenium.server.BrowserConfigurationOptions;
import org.openqa.selenium.server.RemoteControlConfiguration;

public class SafariFileBasedLauncher extends SafariCustomProfileLauncher {

    public SafariFileBasedLauncher(BrowserConfigurationOptions browserOptions,
                                   RemoteControlConfiguration configuration, String sessionId, String browserLaunchLocation) {
        super(browserOptions, configuration, sessionId, browserLaunchLocation);
    }
    
    @Override
    protected void launch(String url) {
        final String fileUrl;
        String query;

        query = LauncherUtils.getQueryString(url);
        query += "&driverUrl=http://localhost:" + getPort() + "/selenium-server/driver/";
        try {
            if (browserConfigurationOptions.is("ensureCleanSession")) {
                ensureCleanSession();
            }
            fileUrl = createExtractedFiles().toURL() + "?" + query;

            launchSafari(fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createExtractedFiles() {
        final File userExtensionsJavascriptFile;
        final File userExtensions;
        final File coreDir;

        coreDir = new File(customProfileDir, "core");
        try {
            coreDir.mkdirs();
            ResourceExtractor.extractResourcePath(SafariFileBasedLauncher.class, "/core", coreDir);
            // custom user-extensions
            userExtensions = browserConfigurationOptions.getFile("userExtensions");
            if (userExtensions != null) {
                userExtensionsJavascriptFile = new File(coreDir, "scripts/user-extensions.js");
                FileHandler.copy(userExtensions, userExtensionsJavascriptFile);
            }
            return new File(coreDir, "RemoteRunner.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
