package com.vinhuni.senselib.controller

import com.vinhuni.senselib.service.AuthorService
import com.vinhuni.senselib.service.BookService
import com.vinhuni.senselib.service.CategoryService
import com.vinhuni.senselib.service.PublisherService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import com.vinhuni.senselib.model.Book
import com.vinhuni.senselib.model.BookAuthor
import com.vinhuni.senselib.model.BookAuthorId
import com.vinhuni.senselib.service.BookAuthorService
import com.vinhuni.senselib.service.UserService
import org.springframework.util.StringUtils
import java.nio.file.Paths


import java.time.Instant
@Controller
class UploadController(
    private val bookService: BookService,
    private val categoryService: CategoryService,
    private val authorService: AuthorService,
    private val publisherService: PublisherService,
    private val userService: UserService,
    private val bookAuthorService: BookAuthorService
) {
    @GetMapping("/upload")
    fun showUploadPage(model: Model, authentication: Authentication?): String {
        if (authentication != null) {
            model.addAttribute("username", authentication.name)
        } else {
            return "redirect:/login"
        }
        model.addAttribute("categories", categoryService.getAllActiveCategories())
        model.addAttribute("authors", authorService.getAllAuthors())
        model.addAttribute("publishers", publisherService.getAllPublishers())
        return "upload"
    }
    @PostMapping("/upload")
    fun uploadDocument(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("coverImage", required = false) coverImage: MultipartFile?,
        @RequestParam("title") title: String,
        @RequestParam("authorId") authorId: Int,
        @RequestParam("publisherId") publisherId: Int,
        @RequestParam("categoryId") categoryId: Int,
        @RequestParam("description") description: String,
        @RequestParam("publicationYear", required = false) publicationYear: Int?,
        @RequestParam("pageCount", required = false) pageCount: Int?,
        @RequestParam("isbn", required = false) isbn: String?,
        authentication: Authentication,
        redirectAttributes: RedirectAttributes
    ): String {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn một tệp tài liệu")
            return "redirect:/upload"
        }

        try {
            // Log để debug
            println("Bắt đầu xử lý tải lên với file: ${file.originalFilename}, kích thước: ${file.size}")

            // Get current user
            val username = authentication.name
            val user = userService.getUserByUsername(username)
                ?: throw IllegalStateException("Không tìm thấy thông tin người dùng")

            // Lưu trữ trong thư mục static
            val rootPath = System.getProperty("user.dir")
            val staticPath = Paths.get(rootPath, "src", "main", "resources", "static")
            val uploadPath = Paths.get(staticPath.toString(), "/uploads")
            val imgPath = Paths.get(staticPath.toString(), "/img")

            println("Đường dẫn uploads: $uploadPath")
            println("Kiểm tra thư mục uploads tồn tại: ${Files.exists(uploadPath)}")

            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                println("Tạo thư mục uploads")
                Files.createDirectories(uploadPath)
            }
            if (!Files.exists(imgPath)) {
                println("Tạo thư mục img")
                Files.createDirectories(imgPath)
            }

            // Xử lý tệp tài liệu với tên duy nhất
            val uniqueFilename = System.currentTimeMillis().toString() + "_" +
                    StringUtils.cleanPath(file.originalFilename ?: "document.pdf")
            val documentPath = uploadPath.resolve(uniqueFilename)

            // Đường dẫn URL tương đối
            val documentRelativePath = "/uploads/$uniqueFilename"

            // Lưu tài liệu
            Files.copy(file.inputStream, documentPath, StandardCopyOption.REPLACE_EXISTING)
            println("Đã lưu tài liệu vào: $documentPath")

            // Xử lý ảnh bìa nếu được cung cấp
            var coverImageRelativePath: String? = null
            if (coverImage != null && !coverImage.isEmpty) {
                val uniqueImageName = System.currentTimeMillis().toString() + "_" +
                        StringUtils.cleanPath(coverImage.originalFilename ?: "cover.jpg")
                val imagePath = imgPath.resolve(uniqueImageName)

                // Đường dẫn URL tương đối
                coverImageRelativePath = "/img/$uniqueImageName"

                // Lưu tệp ảnh
                Files.copy(coverImage.inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING)
                println("Đã lưu ảnh bìa vào: $imagePath")
            }

            // Tạo bản ghi sách mới - KHÔNG gán ID
            val book = Book().apply {
                this.title = title
                this.category = categoryService.getCategoryById(categoryId)
                this.publisher = publisherService.getPublisherById(publisherId)
                this.publicationYear = publicationYear
                this.isbn = isbn
                this.description = description
                this.coverImage = coverImageRelativePath
                this.filePath = documentRelativePath
                this.fileSize = file.size.toInt()
                this.status = "unavailable"
                this.pageCount = pageCount
                this.createdAt = Instant.now()
                this.updatedAt = Instant.now()
                this.createdBy = user
                this.viewCount = 0
            }

            println("Book đã tạo, chuẩn bị lưu vào DB")
            val savedBook = bookService.saveBook(book)
            println("Book đã lưu với ID: ${savedBook.id}")

            // Kết nối sách với tác giả - dùng ID từ savedBook
            val bookAuthor = BookAuthor().apply {
                id = BookAuthorId().apply {
                    this.bookId = savedBook.id
                    this.authorId = authorId
                }
                this.book = savedBook
                this.author = authorService.getAuthorById(authorId)
            }
            bookAuthorService.saveBookAuthor(bookAuthor)
            println("Đã lưu quan hệ BookAuthor")

            redirectAttributes.addFlashAttribute("success", "Tài liệu đã được tải lên thành công")
            return "redirect:/document-detail/" + savedBook.id

        } catch (e: Exception) {
            e.printStackTrace()
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi tải lên: ${e.message}")
            return "redirect:/upload"
        }
    }
}