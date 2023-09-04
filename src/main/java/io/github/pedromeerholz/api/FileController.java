package io.github.pedromeerholz.api;

import io.github.pedromeerholz.aws.S3RequestHandler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private final S3RequestHandler s3RequestHandler;

    public FileController(S3RequestHandler s3RequestHandler) {
        this.s3RequestHandler = s3RequestHandler;
    }

    @PostMapping(value = "/upload/{fileName}")
    public ResponseEntity<String> updaloadFile(@PathVariable("fileName") String fileName, @RequestHeader("FilePath") String filePath) {
        return this.s3RequestHandler.storageFileInBucket(fileName, filePath);
    }

    @GetMapping(value = "/getFile/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody ResponseEntity<byte[]> getImage(@PathVariable("fileName") String fileName) {
        return this.s3RequestHandler.getFile(fileName);
    }
}
