package pl.edu.ur.quizserver.web.controller;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.pdflib.QuizPDFLibrary;
import pl.edu.ur.quizserver.web.dto.CreateGroupDto;
import pl.edu.ur.quizserver.web.service.GroupService;
import pl.edu.ur.quizserver.web.util.GenericResponse;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Transactional
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * Adds a group.
     *
     * @param createGroupDto the DTO containing information about the group to be created
     * @return a GenericResponse indicating the result of the operation
     */
    @PreAuthorize("hasAuthority('permission.group.add_group')")
    @PostMapping("/add")
    public GenericResponse addGroup(@Valid @RequestBody CreateGroupDto createGroupDto){
        groupService.createGroup(createGroupDto.getName());
        return new GenericResponse("success");
    }

    /**
     * Deletes a group.
     *
     * @param id the ID of the group to be deleted
     * @return a GenericResponse indicating the result of the operation
     */
    @PreAuthorize("hasAuthority('permission.group.delete_group')")
    @DeleteMapping("/delete")
    public GenericResponse addGroup(@RequestParam("id") long id){
        groupService.deleteGroup(id);
        return new GenericResponse("success");
    }

    /**
     * Adds a person to a group.
     *
     * @param groupId  the ID of the group to which the person will be added
     * @param personId the ID of the person to be added to the group
     * @return a GenericResponse indicating the result of the operation
     */
    @PreAuthorize("hasAuthority('permission.group.add_person_group')")
    @PutMapping("/addPerson")
    public GenericResponse addToGroup(@RequestParam("groupId") long groupId,
                                      @RequestParam("personId") long personId){
        groupService.addPersonToGroup(groupId,personId);
        return new GenericResponse("success");
    }

    /**
     * Removes a person from a group.
     *
     * @param groupId  the ID of the group from which the person will be removed
     * @param personId the ID of the person to be removed from the group
     * @return a GenericResponse indicating the result of the operation
     */
    @PreAuthorize("hasAuthority('permission.group.remove_person_group')")
    @DeleteMapping("/deletePerson")
    public GenericResponse removeFromGroup(@RequestParam("groupId") long groupId,
                                           @RequestParam("personId") long personId){
        groupService.removePersonFromGroup(groupId,personId);
        return new GenericResponse("success");
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf() {
        try {
            byte[] pdfBytes = QuizPDFLibrary.GeneratePDFForStudents(groupService.mapToDTO(groupService.getGroupedPeople()));
            return ResponseEntity.ok().body(pdfBytes);
        } catch (DocumentException e) {
            return ResponseEntity.status(500).body(null);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
