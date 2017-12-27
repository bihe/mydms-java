package net.binggl.mydms.features.gdrive.client;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import net.binggl.mydms.features.gdrive.GDriveRuntimeException;
import net.binggl.mydms.features.gdrive.models.GDriveCredential;
import net.binggl.mydms.features.gdrive.models.GDriveFile;
import net.binggl.mydms.features.gdrive.models.GDriveItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GDriveClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveClient.class);
    private static final String APPLICATION_NAME = "mydms-java app";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String GOOGLE_DRIVE_FOLDER = "application/vnd.google-apps.folder";
    private static boolean INIT = false;
    private static HttpTransport HTTP_TRANSPORT;

    private AuthorizationCodeFlow authFlow;
    private Drive driveService = null;

    private String clientId;
    private String clientSecret;
    private String redirectUrl;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            INIT = true;
        } catch (Throwable t) {
            LOGGER.error("Could not create a trusted HTTP Transport {}", t.getMessage(), t);
        }
    }

    public GDriveClient(@Value("${google.clientId}") String driveClientId,
                        @Value("${google.clientSecret}") String driveClientSecret,
                        @Value("${google.redirectUrl}") String redirectUrl) {
        this.clientId = driveClientId;
        this.clientSecret = driveClientSecret;
        this.redirectUrl = redirectUrl;

        this.authFlow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(), this.clientId, this.clientSecret,
                Collections.singleton(DriveScopes.DRIVE_FILE)).setAccessType("offline").setApprovalPrompt("force")
                .build();
    }

    public String getRedirectUrl(String correlationToken) {
        checkInit();
        String url = null;
        try {
            url = this.authFlow.newAuthorizationUrl().setRedirectUri(this.redirectUrl).setState(correlationToken)
                    .build();
        } catch (Exception EX) {
            LOGGER.error("Could not get a redirect URL {}", EX.getMessage(), EX);
            throw new GDriveRuntimeException(EX);
        }
        return url;
    }

    public GDriveCredential getCredentials(String authorizationCode, String userId) {
        checkInit();
        GDriveCredential credentials = null;
        try {
            TokenResponse response = this.authFlow.newTokenRequest(authorizationCode)
                    .setRedirectUri(this.redirectUrl).execute();
            Credential cred = this.authFlow.createAndStoreCredential(response, userId);
            credentials = new GDriveCredential(cred.getAccessToken(), cred.getRefreshToken());

        } catch (Exception EX) {
            LOGGER.error("Could not get OAUTH credentials {}", EX.getMessage(), EX);
            throw new GDriveRuntimeException(EX);
        }
        return credentials;
    }

    public Optional<GDriveItem> getFolder(GDriveCredential credentials, String folderName, String parent) {
        this.setupDrive(credentials);
        Optional<GDriveItem> folderItem = Optional.empty();
        try {
            Optional<File> folder = this.getItem(folderName, parent, (name, parentId) -> {
                String query = String.format("mimeType = '%s' and '%s' in parents and name = '%s' and explicitlyTrashed = false",
                        GOOGLE_DRIVE_FOLDER, parentId, name);
                return query;
            });
            if(folder.isPresent()) {
                GDriveItem item = new GDriveItem(folder.get().getId(), folderName, parent, GOOGLE_DRIVE_FOLDER);
                folderItem = Optional.of(item);
            }
        } catch (Exception EX) {
            LOGGER.error("Could not check for folder {}, {}", folderName, EX.getMessage(), EX);
            throw new GDriveRuntimeException(EX);
        }
        return folderItem;
    }

    public Optional<GDriveFile> getFile(GDriveCredential credentials, String path, String parent) {
        this.setupDrive(credentials);
        Optional<GDriveFile> contentFile = Optional.empty();

        try {
            // the supplied path is a /Directory/Filename combination
            // first we need the folder
            List<String> pathElements = this.getPathElements(path);
            String folderName = pathElements.get(0);
            String fileName = pathElements.get(1);
            Optional<File> folder = this.getItem(folderName, parent, (name, parentId) -> {
                String query = String.format("mimeType = '%s' and '%s' in parents and name = '%s' and explicitlyTrashed = false",
                        GOOGLE_DRIVE_FOLDER, parentId, name);
                return query;
            });
            if(folder.isPresent()) {
                // query the file
                String folderId = folder.get().getId();
                Optional<File> file = this.getItem(fileName, folderId, (name, parentId) -> {
                    String query = String.format("mimeType != '%s' and '%s' in parents and name = '%s' and explicitlyTrashed = false",
                            GOOGLE_DRIVE_FOLDER, parentId, name);
                    return query;
                });
                if(file.isPresent()) {
                    byte[] payload = null;
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        this.driveService.files().get(file.get().getId()).executeMediaAndDownloadTo(outputStream);
                        payload = outputStream.toByteArray();
                    }

                    GDriveFile content = new GDriveFile();
                    content.setId(file.get().getId());
                    content.setName(file.get().getName());
                    content.setParent(folderId);
                    content.setPayload(payload);
                    content.setMimeType(file.get().getMimeType());

                    contentFile = Optional.of(content);
                }
            }

        } catch (Exception EX) {
            LOGGER.error("Could not get file {}, {}", path, EX.getMessage(), EX);
            throw new GDriveRuntimeException(EX);
        }
        return contentFile;
    }

    public GDriveItem createFolder(GDriveCredential credentials, String folderName, String parent) {
        this.setupDrive(credentials);
        GDriveItem item = null;
        try {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            List<String> parents = new ArrayList<>();
            parents.add(parent);
            fileMetadata.setParents(parents);
            fileMetadata.setMimeType(GOOGLE_DRIVE_FOLDER);

            File file = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            if(file != null) {
                item = new GDriveItem(file.getId(), folderName, parent, GOOGLE_DRIVE_FOLDER);
            }

        } catch (Exception EX) {
            LOGGER.error("Could not create for folder {}, {}", folderName, EX.getMessage(), EX);
            throw new GDriveRuntimeException(EX);
        }
        return item;
    }

    public GDriveItem saveItem(GDriveCredential credentials, String fileName, String contentType, byte[] payload, String parent) {
        this.setupDrive(credentials);
        GDriveItem item = null;
        try {
            // check if the item is already available
            Optional<File> driveItem = this.getItem(fileName, parent,() -> { return "files(id, name)"; },(name, parentId) -> {

                String query = String.format("mimeType != '%s' and '%s' in parents and name = '%s' and explicitlyTrashed = false",
                        GOOGLE_DRIVE_FOLDER, parentId, name);
                return query;
            });


            ByteArrayContent content =  new ByteArrayContent(null, payload);
            File file = null;
            if(driveItem.isPresent()) {
                LOGGER.info("Will udpate existing item {}", driveItem.get().getId());
                file = driveService.files().update(driveItem.get().getId(), null, content)
                        .setFields("id")
                        .execute();
            } else {
                LOGGER.info("Will create a new file {}", fileName);

                File fileMetadata = new File();
                fileMetadata.setName(fileName);
                fileMetadata.setParents(Collections.singletonList(parent));

                file = driveService.files().create(fileMetadata, content)
                        .setFields("id, mimeType")
                        .execute();
            }

            if(file != null) {
                item = new GDriveItem(file.getId(), fileName, parent, file.getMimeType());
            }
        } catch (Exception EX) {
            LOGGER.error("Could not save item {}, {}", fileName, EX.getMessage(), EX);
            throw new GDriveRuntimeException(EX);
        }
        return item;
    }





    private Optional<File> getItem(String name, String parent, QueryLambdaFunction queryCallback) throws IOException {
        return this.getItem(name, parent, () -> {
            return "files(id, name, mimeType)";
        }, queryCallback);
    }

    private Optional<File> getItem(String name, String parent, FieldLambdaFunction fieldsCallback, QueryLambdaFunction queryCallback) throws IOException {
        Optional<File> item = Optional.empty();

        String query = queryCallback.apply(name, parent);
        String fields = fieldsCallback.apply();

        FileList result = this.driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields(fields)
                .execute();
        if(result != null && result.getFiles() != null) {
            item = result.getFiles().stream().filter(e -> name.equals(e.getName())).findAny();
        }
        return item;
    }

    private void setupDrive(GDriveCredential credentials) {
        if (driveService == null) {
            Credential cred = this.getGoogleCredentials(credentials);

            LOGGER.debug("Will setup drive service!");
            this.driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                    .setApplicationName(APPLICATION_NAME).build();
        }
    }

    private Credential getGoogleCredentials(GDriveCredential credentials) {
        checkInit();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(this.clientId, this.clientSecret)
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .build();

        credential.setAccessToken(credentials.getAccessToken());
        credential.setRefreshToken(credentials.getRefreshToken());

        return credential;
    }

    private List<String> getPathElements(String path) {
        String[] rawElements = path.split(Pattern.quote("/"));
        List<String> parts = new ArrayList<>();
        Collections.addAll(parts, rawElements);
        return parts.stream().filter(item -> StringUtils.isNotEmpty(item)).collect(Collectors.toList());
    }

    private static void checkInit() throws RuntimeException {
        if (!INIT) {
            throw new IllegalStateException("The drive application was not initialized properyl!");
        }
    }

    @FunctionalInterface
    private interface QueryLambdaFunction {
        String apply(String name, String parentId);
    }

    @FunctionalInterface
    private interface FieldLambdaFunction {
        String apply();
    }

}
