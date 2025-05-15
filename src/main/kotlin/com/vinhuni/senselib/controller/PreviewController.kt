package com.vinhuni.senselib.controller
import com.vinhuni.senselib.service.BookService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.FileInputStream
import java.nio.file.Paths
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.File

@Controller
class PreviewController @Autowired constructor(
    private val bookService: BookService
) {
    private val logger = LoggerFactory.getLogger(PreviewController::class.java)

    @GetMapping("/preview/{bookId}")
    fun previewPdf(
        @PathVariable bookId: Int,
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): ResponseEntity<*> {
        logger.info("Yêu cầu xem trước tài liệu ID: {}", bookId)

        if (authentication == null) {
            logger.warn("Người dùng chưa đăng nhập")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn cần đăng nhập để xem tài liệu")
        }

        try {
            // Lấy thông tin sách từ database
            val book = bookService.getBookById(bookId)
                ?: return ResponseEntity.notFound().build<Any>()

            logger.info("Tìm thấy sách: {}", book.title)

            // Lấy đường dẫn của file từ database
            val filePath = book.filePath
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy file của tài liệu")

            logger.info("Đường dẫn tương đối của file: {}", filePath)

            // Chuyển đường dẫn tương đối thành đường dẫn tuyệt đối
            val rootPath = System.getProperty("user.dir")
            val absolutePath = if (filePath.startsWith("/")) {
                Paths.get(rootPath, "src", "main", "resources", "static${filePath}").toString()
            } else {
                Paths.get(rootPath, "src", "main", "resources", "static", filePath).toString()
            }

            logger.info("Đường dẫn tuyệt đối của file: {}", absolutePath)

            val file = File(filePath)
            if (!file.exists()) {
                // Try alternate paths
                val rootPath = System.getProperty("user.dir")
                val alternateFilePath = Paths.get(rootPath, "src", "main", "resources", "static", filePath).toString()
                val alternateFile = File(alternateFilePath)

                if (!alternateFile.exists()) {
                    logger.error("File not found at either path: {} or {}", filePath, alternateFilePath)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PDF file not found")
                }

                logger.info("File found at alternate path: {}", alternateFilePath)
                val resource = FileSystemResource(alternateFile)

                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_PDF
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${alternateFile.name}")

                return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource)
            }
            logger.info("File found at original path: {}", filePath)

            val resource = FileSystemResource(file)
            val headers = HttpHeaders()

            headers.contentType = MediaType.APPLICATION_PDF
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=${file.name}")
            book.viewCount = book.viewCount?.plus(1) ?: 1
            bookService.saveBook(book)
            return ResponseEntity.ok()
                .headers(headers)
                .body(resource)

        } catch (e: Exception) {
            logger.error("Lỗi khi xem trước tài liệu: {}", e.message, e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Có lỗi xảy ra khi tải tài liệu: ${e.message}")
        }
    }
}