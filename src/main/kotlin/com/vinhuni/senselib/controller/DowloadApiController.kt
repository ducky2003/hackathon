package com.vinhuni.senselib.controller

import com.vinhuni.senselib.service.DowloadService
import com.vinhuni.senselib.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/api/downloads")
class DowloadApiController (
    private val downloadService: DowloadService,
    private val userService: UserService
    ) {

        @DeleteMapping("/{downloadId}")
        fun deleteDownload(
            @PathVariable downloadId: Int,
            authentication: Authentication,
            redirectAttributes: RedirectAttributes
        ): String {
            try {
                val username = authentication.name
                val user = userService.getUserByUsername(username)
                    ?: return "redirect:/login"

                val download = downloadService.getDownloadById(downloadId)
                    ?: return "redirect:/my-downloads"

                if (download.user?.id != user.id) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xóa lịch sử tải xuống này.")
                    return "redirect:/my-downloads"
                }

                downloadService.softDeleteDownload(downloadId)
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa lịch sử tải xuống thành công.")

                return "redirect:/my-downloads"
            } catch (e: Exception) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xóa lịch sử tải xuống.")
                return "redirect:/my-downloads"
            }
        }

}