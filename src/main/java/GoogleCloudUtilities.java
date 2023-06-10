import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.Collections;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GoogleCloudUtilities {

    private final GoogleCredentials googleVisionCredentials;
    private final GoogleCredentials googleTranslateCredentials;
    private final ImageAnnotatorSettings settings;
    private final Translate translate;

    public GoogleCloudUtilities(String googleVisionCredPath, String googleTranslateCredPath) {
        try {
            googleVisionCredentials = GoogleCredentials.fromStream(Files.newInputStream(Paths.get(googleVisionCredPath)))
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
            settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(googleVisionCredentials))
                    .build();
            googleTranslateCredentials = GoogleCredentials.fromStream(new FileInputStream(googleTranslateCredPath));
            translate = TranslateOptions.newBuilder().setCredentials(googleTranslateCredentials).build().getService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing Google Cloud utilities.");
        }
    }

    public String detectWords(BufferedImage image) {
        try {
            // Create the Vision API client
            try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create(settings)) {
                // Convert the BufferedImage to ByteString
                ByteString imgBytes = convertImageToByteString(image);

                // Build the image request
                Image img = Image.newBuilder().setContent(imgBytes).build();
                Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feature)
                        .setImage(img)
                        .build();

                // Perform the image annotation request
                List<AnnotateImageRequest> requests = new ArrayList<>();
                requests.add(request);

                BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(requests);

                // Process the response and extract the detected words
                List<String> words = new ArrayList<>();
                for (AnnotateImageResponse imageResponse : response.getResponsesList()) {
                    for (EntityAnnotation annotation : imageResponse.getTextAnnotationsList()) {
                        words.add(annotation.getDescription());
                    }
                }

                // Return the detected words as a single string
                return String.join(" ", words);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error detecting words from the image.");
        }
    }

    public String translate(String text, String lang) {
        try {
            Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage(lang));
            return translation.getTranslatedText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error translating the text.");
        }
    }

    private ByteString convertImageToByteString(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return ByteString.copyFrom(imageBytes);
    }
}
