package com.example.springmvc.controller;

import com.example.springmvc.dto.DashboardStats;
import com.example.springmvc.dto.item.AssetResponse;
import com.example.springmvc.dto.item.ChemicalResponse;
import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.dto.user.UserResponse;
import com.example.springmvc.dto.user.CreateUserRequest;
import com.example.springmvc.entity.Room;
import com.example.springmvc.repository.RoomRepository;
import com.example.springmvc.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AssetService assetService;
    private final ChemicalService chemicalService;
    private final ExcelService excelService;
    private final RentService rentService;
    private final UserService userService;
    private final RoomRepository roomRepository;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable("id") Integer id, @RequestParam("isActive") Boolean isActive) {
        return ResponseEntity.ok(userService.updateUserStatus(id, isActive));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(id, currentUsername);
        return ResponseEntity.ok(Map.of("message", "Xóa tài khoản thành công!"));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<Map<String, String>>> getWarehouses() {
        List<Room> warehouses = roomRepository.findByTypeIn(Arrays.asList("WAREHOUSE"));
        return ResponseEntity.ok(warehouses.stream()
                .map(r -> Map.of("roomName", r.getRoomName()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<RentListResponse>> getAllTickets(@RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(rentService.getAllTickets(status));
    }

    @GetMapping("/assets")
    public ResponseEntity<List<AssetResponse>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/assets/{id}")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @PostMapping("/assets")
    public ResponseEntity<AssetResponse> createAsset(@RequestBody AssetResponse dto) {
        return ResponseEntity.ok(assetService.createAsset(dto));
    }

    @PutMapping("/assets/{id}")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable("id") Integer id, @RequestBody AssetResponse dto) {
        return ResponseEntity.ok(assetService.updateAsset(id, dto));
    }

    @DeleteMapping("/assets/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable("id") Integer id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok(Map.of("message", "Xóa thiết bị thành công!"));
    }

    @GetMapping("/chemicals")
    public ResponseEntity<List<ChemicalResponse>> getAllChemicals() {
        return ResponseEntity.ok(chemicalService.getAllChemicals());
    }

    @GetMapping("/chemicals/{id}")
    public ResponseEntity<ChemicalResponse> getChemicalById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(chemicalService.getChemicalById(id));
    }

    @PostMapping("/chemicals")
    public ResponseEntity<ChemicalResponse> createChemical(@RequestBody ChemicalResponse dto) {
        return ResponseEntity.ok(chemicalService.createChemical(dto));
    }

    @PutMapping("/chemicals/{id}")
    public ResponseEntity<ChemicalResponse> updateChemical(@PathVariable("id") Integer id, @RequestBody ChemicalResponse dto) {
        return ResponseEntity.ok(chemicalService.updateChemical(id, dto));
    }

    @DeleteMapping("/chemicals/{id}")
    public ResponseEntity<?> deleteChemical(@PathVariable("id") Integer id) {
        chemicalService.deleteChemical(id);
        return ResponseEntity.ok(Map.of("message", "Xóa hóa chất thành công!"));
    }

    @GetMapping("/export/chemicals")
    public ResponseEntity<byte[]> exportChemicals() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=chemicals.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelService.exportChemicalsToExcel(chemicalService.getAllChemicals()));
    }

    @GetMapping("/export/assets")
    public ResponseEntity<byte[]> exportAssets() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=assets.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelService.exportAssetsToExcel(assetService.getAllAssets()));
    }

    @PostMapping(value = "/import/chemicals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importChemicals(@RequestParam("file") MultipartFile file) {
        List<ChemicalResponse> data = excelService.importChemicalsFromExcel(file);
        int count = 0;
        for (ChemicalResponse dto : data) {
            try {
                chemicalService.importOrUpdateChemical(dto);
                count++;
            } catch (Exception e) {}
        }
        return ResponseEntity.ok(Map.of("message", "Import thành công " + count + " dòng!"));
    }

    @PostMapping(value = "/import/assets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importAssets(@RequestParam("file") MultipartFile file) {
        List<AssetResponse> data = excelService.importAssetsFromExcel(file);
        int count = 0;
        for (AssetResponse dto : data) {
            try {
                assetService.importOrUpdateAsset(dto);
                count++;
            } catch (Exception e) {}
        }
        return ResponseEntity.ok(Map.of("message", "Import thành công " + count + " dòng!"));
    }
}