package io.github.pedromeerholz.aws;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;


@Service
public class S3RequestHandler {
    private final S3Client S3_CLIENT;
    private final String BUCKET_NAME;

    public S3RequestHandler() {
        S3_CLIENT = DependencyFactory.s3Client();
        BUCKET_NAME = "aws-s3-bucket-java-sdk";
    }
    public ResponseEntity saveImage(String imageName, String imagePath) {
        try {
            PutObjectRequest putObject = PutObjectRequest.builder()
                    .bucket(this.BUCKET_NAME)
                    .key(imageName)
                    .build();

            if (!this.verifyIfImageExists(imageName)) {
                this.S3_CLIENT.putObject(putObject, RequestBody.fromFile(new File(imagePath)));
                return new ResponseEntity(HttpStatus.CREATED);
            }
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        } catch (S3Exception exception) {
            System.err.println(exception.getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity renameImage(String currentImageName, String newImageName) {
        try {
            if (this.verifyIfImageExists(currentImageName) && !this.verifyIfImageExists(newImageName)) {
                CopyObjectRequest request = CopyObjectRequest.builder()
                        .sourceBucket(this.BUCKET_NAME)
                        .sourceKey(currentImageName)
                        .destinationBucket(this.BUCKET_NAME)
                        .destinationKey(newImageName)
                        .build();
                this.S3_CLIENT.copyObject(request);
                this.removeImage(currentImageName);
                return new ResponseEntity(HttpStatus.OK);
            }
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        } catch (NoSuchKeyException exception) {
            exception.printStackTrace();
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        } catch (S3Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity getImage(String imageName) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .key(imageName)
                    .bucket(this.BUCKET_NAME)
                    .build();

            ResponseBytes<GetObjectResponse> response = this.S3_CLIENT.getObjectAsBytes(request);
            byte[] file = response.asByteArray();
            return new ResponseEntity(file, HttpStatus.OK);
        } catch (NoSuchKeyException exception) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        } catch (S3Exception exception) {
            System.err.println(exception.getMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity removeImage(String imageName) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .key(imageName)
                    .bucket(this.BUCKET_NAME)
                    .build();

            if (verifyIfImageExists(imageName)) {
                this.S3_CLIENT.deleteObject(request);
                return new ResponseEntity("Imagem removida com sucesso", HttpStatus.OK);
            }
            return new ResponseEntity("A imagem informada não existe", HttpStatus.NOT_ACCEPTABLE);
        } catch (S3Exception exception) {
            return new ResponseEntity("Erro interno", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean verifyIfImageExists(String imageName) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(this.BUCKET_NAME)
                    .key(imageName)
                    .build();
            this.S3_CLIENT.headObject(request);
            return true;
        } catch (NoSuchKeyException exception) {
            return false;
        } catch (S3Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
