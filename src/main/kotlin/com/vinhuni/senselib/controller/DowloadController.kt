package com.vinhuni.senselib.controller

import com.vinhuni.senselib.model.Download
import com.vinhuni.senselib.service.BookService
import com.vinhuni.senselib.service.DowloadService
import com.vinhuni.senselib.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.ResourceLoader
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.FileInputStream
import java.nio.file.Paths
import java.time.Instant

@Controller
class DowloadController (
    private val bookService: BookService,
    private val userService: UserService,
    private val downloadService: DowloadService,
    private val resourceLoader: ResourceLoader
    ) {

        @GetMapping("/download/{bookId}")
        fun downloadBook(
            @PathVariable bookId: Int,
            authentication: Authentication,
            request: HttpServletRequest,
            response: HttpServletResponse
        ) {
            // Kiểm tra người dùng đã đăng nhập
            if (authentication == null) {
                response.sendRedirect("/login")
                return
            }

            // Lấy thông tin sách
            val book = bookService.getBookById(bookId)
            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy tài liệu")
                return
            }

            // Lấy thông tin người dùng
            val username = authentication.name
            val user = userService.getUserByUsername(username)
                ?: throw IllegalStateException("Không tìm thấy thông tin người dùng")

            try {
                // Lấy đường dẫn file từ thông tin sách
                val filePath = book.filePath
                if (filePath.isNullOrEmpty()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy file tài liệu")
                    return
                }

                // Tạo đường dẫn tuyệt đối đến file
                val rootPath = System.getProperty("user.dir")
                val staticPath = Paths.get(rootPath, "src", "main", "resources", "static")
                // Loại bỏ dấu / ở đầu filePath nếu có
                val cleanFilePath = if (filePath.startsWith("/")) filePath.substring(1) else filePath
                val fullPath = staticPath.resolve(cleanFilePath).toFile()

                if (!fullPath.exists()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy file tài liệu")
                    return
                }

                // Cấu hình response
                response.contentType = "application/octet-stream"
                response.setHeader("Content-Disposition", "attachment; filename=${fullPath.name}")
                response.setContentLength(fullPath.length().toInt())

                // Copy file vào response output stream
                FileInputStream(fullPath).use { inputStream ->
                    FileCopyUtils.copy(inputStream, response.outputStream)
                }

                // Lưu lịch sử tải xuống
                val download = Download().apply {
                    this.book = book
                    this.user = user
                    this.downloadDate = Instant.now()
                    this.ipAddress = request.remoteAddr
                }

                downloadService.saveDownload(download)

            } catch (e: Exception) {
                e.printStackTrace()
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tải tài liệu: ${e.message}")
            }
        }

        @GetMapping("/my-downloads")
        fun showMyDownloads(model: Model, authentication: Authentication): String {
            if (authentication == null) {
                return "redirect:/login"
            }

            val username = authentication.name
            val user = userService.getUserByUsername(username)
                ?: throw IllegalStateException("Không tìm thấy thông tin người dùng")

            val downloads = downloadService.getDownloadsByUser(user.id!!)

            model.addAttribute("downloads", downloads)
            model.addAttribute("username", username)

            return "downloads"
        }
}