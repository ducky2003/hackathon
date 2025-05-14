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
        @RequestParam("coverImage") coverImage: MultipartFile?,
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
            // Get current user
            val username = authentication.name
            val user = userService.getUserByUsername(username)
                ?: throw IllegalStateException("Không tìm thấy thông tin người dùng")

            // Handle document file
            val fileDir = "uploads/documents/"
            val fileName = StringUtils.cleanPath(file.originalFilename ?: "document.pdf")
            val uniqueFileName = System.currentTimeMillis().toString() + "_" + fileName
            val filePath = fileDir + uniqueFileName

            // Handle cover image if provided
            var coverImagePath: String? = null
            if (coverImage != null && !coverImage.isEmpty) {
                val imgDir = "uploads/covers/"
                val imgFileName = StringUtils.cleanPath(coverImage.originalFilename ?: "cover.jpg")
                val uniqueImgName = System.currentTimeMillis().toString() + "_" + imgFileName
                coverImagePath = imgDir + uniqueImgName
                Files.createDirectories(Path.of(imgDir))
                Files.copy(
                    coverImage.inputStream,
                    Path.of(imgDir, uniqueImgName),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }

            // Save document file
            Files.createDirectories(Path.of(fileDir))
            Files.copy(
                file.inputStream,
                Path.of(fileDir, uniqueFileName),
                StandardCopyOption.REPLACE_EXISTING
            )

            // Create new book record
            val book = Book().apply {
                this.title = title
                this.category = categoryService.getCategoryById(categoryId)
                this.publisher = publisherService.getPublisherById(publisherId)
                this.publicationYear = publicationYear
                this.isbn = isbn
                this.description = description
                this.coverImage = coverImagePath
                this.filePath = filePath
                this.fileSize = file.size.toInt()
                this.status = "waiting"
                this.pageCount = pageCount
                this.createdAt = Instant.now()
                this.updatedAt = Instant.now()
                this.createdBy = user
                this.viewCount = 0
            }


            val savedBook = bookService.saveBook(book)

            // Connect book with author
            val bookAuthor = BookAuthor().apply {
                id = BookAuthorId().apply {
                    this.bookId = savedBook.id
                    this.authorId = authorId
                }
                this.book = savedBook
                this.author = authorService.getAuthorById(authorId)
            }
            bookAuthorService.saveBookAuthor(bookAuthor)

            redirectAttributes.addFlashAttribute("success", "Tài liệu đã được tải lên thành công")
            return "redirect:/document-detail/" + savedBook.id

        } catch (e: Exception) {
            e.printStackTrace()
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi tải lên: ${e.message}")
            return "redirect:/upload"
        }
    }
}