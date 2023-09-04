package io.github.pedromeerholz.api;

import io.github.pedromeerholz.aws.S3RequestHandler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/image")
public class FileController {
    private final S3RequestHandler s3RequestHandler;

    public FileController(S3RequestHandler s3RequestHandler) {
        this.s3RequestHandler = s3RequestHandler;
    }

    @PostMapping(value = "/upload/{imageName}")
    public ResponseEntity<String> updaloadImage(@PathVariable("imageName") String imageName, @RequestHeader("ImagePath") String imagePath) {
        return this.s3RequestHandler.saveImage(imageName, imagePath);
    }

    @GetMapping(value = "/getFile/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody ResponseEntity<byte[]> getImage(@PathVariable("imageName") String imageName) {
        return this.s3RequestHandler.getImage(imageName);
    }

    @DeleteMapping(value = "/remove/{imageName}")
    public ResponseEntity<String> removeImage(@PathVariable("imageName") String imageName) {
        return this.s3RequestHandler.removeImage(imageName);
    }
}
