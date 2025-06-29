package com.exalt.courier.regional.controller;

import com.socialecommerceecosystem.regional.model.RegionalAdmin;
import com.socialecommerceecosystem.regional.service.RegionalAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing regional administrators.
 */
@RestController
@RequestMapping("/api/v1/regional-admins")
@RequiredArgsConstructor
@Slf4j
public class RegionalAdminController {

    private final RegionalAdminService regionalAdminService;

    /**
     * GET /api/v1/regional-admins : Get all regional admins
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins
     */
    @GetMapping
    public ResponseEntity<List<RegionalAdmin>> getAllRegionalAdmins() {
        log.debug("REST request to get all regional admins");
        return ResponseEntity.ok(regionalAdminService.getAllRegionalAdmins());
    }

    /**
     * GET /api/v1/regional-admins/active : Get all active regional admins
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active regional admins
     */
    @GetMapping("/active")
    public ResponseEntity<List<RegionalAdmin>> getActiveRegionalAdmins() {
        log.debug("REST request to get all active regional admins");
        return ResponseEntity.ok(regionalAdminService.getActiveRegionalAdmins());
    }

    /**
     * GET /api/v1/regional-admins/{id} : Get a regional admin by id
     * 
     * @param id the id of the regional admin to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional admin, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionalAdmin> getRegionalAdmin(@PathVariable Long id) {
        log.debug("REST request to get regional admin : {}", id);
        return regionalAdminService.getRegionalAdminById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional admin not found with id: " + id));
    }

    /**
     * GET /api/v1/regional-admins/username/{username} : Get a regional admin by username
     * 
     * @param username the username of the regional admin to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional admin, or with status 404 (Not Found)
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<RegionalAdmin> getRegionalAdminByUsername(@PathVariable String username) {
        log.debug("REST request to get regional admin by username : {}", username);
        return regionalAdminService.getRegionalAdminByUsername(username)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional admin not found with username: " + username));
    }

    /**
     * GET /api/v1/regional-admins/email/{email} : Get a regional admin by email
     * 
     * @param email the email of the regional admin to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional admin, or with status 404 (Not Found)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<RegionalAdmin> getRegionalAdminByEmail(@PathVariable String email) {
        log.debug("REST request to get regional admin by email : {}", email);
        return regionalAdminService.getRegionalAdminByEmail(email)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional admin not found with email: " + email));
    }

    /**
     * GET /api/v1/regional-admins/region/{regionalSettingsId} : Get regional admins by regional settings ID
     * 
     * @param regionalSettingsId the ID of the regional settings
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins for the regional settings
     */
    @GetMapping("/region/{regionalSettingsId}")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByRegionalSettingsId(@PathVariable Long regionalSettingsId) {
        log.debug("REST request to get regional admins for regional settings : {}", regionalSettingsId);
        try {
            return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByRegionalSettingsId(regionalSettingsId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/regional-admins/role/{role} : Get regional admins by role
     * 
     * @param role the role to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins with the role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByRole(@PathVariable String role) {
        log.debug("REST request to get regional admins by role : {}", role);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByRole(role));
    }

    /**
     * POST /api/v1/regional-admins/roles/search : Get regional admins by multiple roles
     * 
     * @param roles the set of roles to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins having any of the roles
     */
    @PostMapping("/roles/search")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByRoles(@RequestBody Set<String> roles) {
        log.debug("REST request to get regional admins by roles : {}", roles);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByRoles(roles));
    }

    /**
     * GET /api/v1/regional-admins/logged-in-since : Get regional admins logged in since a date
     * 
     * @param date the date to compare against
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins who have logged in since the date
     */
    @GetMapping("/logged-in-since")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsLoggedInSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.debug("REST request to get regional admins logged in since : {}", date);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsLoggedInSince(date));
    }

    /**
     * GET /api/v1/regional-admins/not-logged-in-since : Get regional admins not logged in since a date
     * 
     * @param date the date to compare against
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins who have not logged in since the date
     */
    @GetMapping("/not-logged-in-since")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsNotLoggedInSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.debug("REST request to get regional admins not logged in since : {}", date);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsNotLoggedInSince(date));
    }

    /**
     * GET /api/v1/regional-admins/expiring-accounts : Get regional admins with expiring accounts
     * 
     * @param date the date to compare against
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins with accounts expiring before the date
     */
    @GetMapping("/expiring-accounts")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsWithExpiringAccounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.debug("REST request to get regional admins with accounts expiring before : {}", date);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsWithExpiringAccounts(date));
    }

    /**
     * GET /api/v1/regional-admins/department/{department} : Get regional admins by department
     * 
     * @param department the department to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins in the department
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByDepartment(@PathVariable String department) {
        log.debug("REST request to get regional admins by department : {}", department);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByDepartment(department));
    }

    /**
     * GET /api/v1/regional-admins/position/{position} : Get regional admins by position
     * 
     * @param position the position to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins with the position
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByPosition(@PathVariable String position) {
        log.debug("REST request to get regional admins by position : {}", position);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByPosition(position));
    }

    /**
     * GET /api/v1/regional-admins/access-level/{accessLevel} : Get regional admins by access level
     * 
     * @param accessLevel the access level to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins with the access level
     */
    @GetMapping("/access-level/{accessLevel}")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByAccessLevel(@PathVariable Integer accessLevel) {
        log.debug("REST request to get regional admins by access level : {}", accessLevel);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByAccessLevel(accessLevel));
    }

    /**
     * GET /api/v1/regional-admins/manager/{managerId} : Get regional admins reporting to a manager
     * 
     * @param managerId the ID of the manager
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins reporting to the manager
     */
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsByManager(@PathVariable Long managerId) {
        log.debug("REST request to get regional admins reporting to manager : {}", managerId);
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsByManager(managerId));
    }

    /**
     * GET /api/v1/regional-admins/two-factor-enabled : Get regional admins with two-factor authentication enabled
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins with two-factor authentication enabled
     */
    @GetMapping("/two-factor-enabled")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsWithTwoFactorEnabled() {
        log.debug("REST request to get regional admins with two-factor authentication enabled");
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsWithTwoFactorEnabled());
    }

    /**
     * GET /api/v1/regional-admins/locked-accounts : Get regional admins with locked accounts
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional admins with locked accounts
     */
    @GetMapping("/locked-accounts")
    public ResponseEntity<List<RegionalAdmin>> getRegionalAdminsWithLockedAccounts() {
        log.debug("REST request to get regional admins with locked accounts");
        return ResponseEntity.ok(regionalAdminService.getRegionalAdminsWithLockedAccounts());
    }

    /**
     * GET /api/v1/regional-admins/search : Search regional admins by name
     * 
     * @param searchText the text to search for in regional admin names
     * @return the ResponseEntity with status 200 (OK) and the list of matching regional admins
     */
    @GetMapping("/search")
    public ResponseEntity<List<RegionalAdmin>> searchRegionalAdminsByName(@RequestParam String searchText) {
        log.debug("REST request to search regional admins by name containing : {}", searchText);
        return ResponseEntity.ok(regionalAdminService.searchRegionalAdminsByName(searchText));
    }

    /**
     * POST /api/v1/regional-admins : Create a new regional admin
     * 
     * @param regionalAdmin the regional admin to create
     * @return the ResponseEntity with status 201 (Created) and with body the new regional admin
     */
    @PostMapping
    public ResponseEntity<RegionalAdmin> createRegionalAdmin(@Valid @RequestBody RegionalAdmin regionalAdmin) {
        log.debug("REST request to save regional admin : {}", regionalAdmin);
        if (regionalAdmin.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new regional admin cannot already have an ID");
        }
        
        try {
            RegionalAdmin result = regionalAdminService.createRegionalAdmin(regionalAdmin);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/regional-admins/{id} : Update an existing regional admin
     * 
     * @param id the id of the regional admin to update
     * @param regionalAdmin the regional admin to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionalAdmin> updateRegionalAdmin(
            @PathVariable Long id, 
            @Valid @RequestBody RegionalAdmin regionalAdmin) {
        log.debug("REST request to update regional admin : {}", regionalAdmin);
        if (regionalAdmin.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Regional admin ID must not be null");
        }
        if (!id.equals(regionalAdmin.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            RegionalAdmin result = regionalAdminService.updateRegionalAdmin(id, regionalAdmin);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/regional-admins/{id} : Delete a regional admin
     * 
     * @param id the id of the regional admin to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegionalAdmin(@PathVariable Long id) {
        log.debug("REST request to delete regional admin : {}", id);
        try {
            regionalAdminService.deleteRegionalAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/add-roles : Add roles to a regional admin
     * 
     * @param id the id of the regional admin
     * @param roles the roles to add
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/add-roles")
    public ResponseEntity<RegionalAdmin> addRolesToRegionalAdmin(
            @PathVariable Long id, 
            @RequestBody Set<String> roles) {
        log.debug("REST request to add roles {} to regional admin : {}", roles, id);
        try {
            RegionalAdmin result = regionalAdminService.addRolesToRegionalAdmin(id, roles);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/remove-roles : Remove roles from a regional admin
     * 
     * @param id the id of the regional admin
     * @param roles the roles to remove
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/remove-roles")
    public ResponseEntity<RegionalAdmin> removeRolesFromRegionalAdmin(
            @PathVariable Long id, 
            @RequestBody Set<String> roles) {
        log.debug("REST request to remove roles {} from regional admin : {}", roles, id);
        try {
            RegionalAdmin result = regionalAdminService.removeRolesFromRegionalAdmin(id, roles);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/update-login : Update the last login date for a regional admin
     * 
     * @param id the id of the regional admin
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/update-login")
    public ResponseEntity<RegionalAdmin> updateLastLoginDate(@PathVariable Long id) {
        log.debug("REST request to update last login date for regional admin : {}", id);
        try {
            RegionalAdmin result = regionalAdminService.updateLastLoginDate(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/lock : Lock a regional admin account
     * 
     * @param id the id of the regional admin
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/lock")
    public ResponseEntity<RegionalAdmin> lockAccount(@PathVariable Long id) {
        log.debug("REST request to lock account for regional admin : {}", id);
        try {
            RegionalAdmin result = regionalAdminService.lockAccount(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/unlock : Unlock a regional admin account
     * 
     * @param id the id of the regional admin
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<RegionalAdmin> unlockAccount(@PathVariable Long id) {
        log.debug("REST request to unlock account for regional admin : {}", id);
        try {
            RegionalAdmin result = regionalAdminService.unlockAccount(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/enable-two-factor : Enable two-factor authentication for a regional admin
     * 
     * @param id the id of the regional admin
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/enable-two-factor")
    public ResponseEntity<RegionalAdmin> enableTwoFactorAuthentication(@PathVariable Long id) {
        log.debug("REST request to enable two-factor authentication for regional admin : {}", id);
        try {
            RegionalAdmin result = regionalAdminService.enableTwoFactorAuthentication(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-admins/{id}/disable-two-factor : Disable two-factor authentication for a regional admin
     * 
     * @param id the id of the regional admin
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin
     */
    @PatchMapping("/{id}/disable-two-factor")
    public ResponseEntity<RegionalAdmin> disableTwoFactorAuthentication(@PathVariable Long id) {
        log.debug("REST request to disable two-factor authentication for regional admin : {}", id);
        try {
            RegionalAdmin result = regionalAdminService.disableTwoFactorAuthentication(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * HEAD /api/v1/regional-admins/username/{username} : Check if a regional admin with the given username exists
     * 
     * @param username the username to check
     * @return the ResponseEntity with status 200 (OK) if exists, or 404 (Not Found) if not
     */
    @RequestMapping(method = RequestMethod.HEAD, path = "/username/{username}")
    public ResponseEntity<Void> checkUsernameExists(@PathVariable String username) {
        log.debug("REST request to check if regional admin exists with username : {}", username);
        return regionalAdminService.existsByUsername(username) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }

    /**
     * HEAD /api/v1/regional-admins/email/{email} : Check if a regional admin with the given email exists
     * 
     * @param email the email to check
     * @return the ResponseEntity with status 200 (OK) if exists, or 404 (Not Found) if not
     */
    @RequestMapping(method = RequestMethod.HEAD, path = "/email/{email}")
    public ResponseEntity<Void> checkEmailExists(@PathVariable String email) {
        log.debug("REST request to check if regional admin exists with email : {}", email);
        return regionalAdminService.existsByEmail(email) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }
}
