package com.osscube.api.utils

import com.osscube.api.domain.exception.file.InvalidFileException
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile

object FileUtil {
    fun uploadFile(multipartFile: MultipartFile, dst: File, allowedMimeTypes: List<MediaType>) {
        val mimeType = MediaType.valueOf(Files.probeContentType(Path(multipartFile.originalFilename!!)))
        if (!allowedMimeTypes.contains(mimeType)) {
            throw InvalidFileException()
        }
        multipartFile.transferTo(dst)
    }
}
