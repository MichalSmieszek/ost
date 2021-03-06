package eu.fp7.driver.ost.driver.web;

import eu.fp7.driver.ost.core.annotation.FindAllGetMapping;
import eu.fp7.driver.ost.core.dto.Dto;
import eu.fp7.driver.ost.core.dto.PageDto;
import eu.fp7.driver.ost.driver.dto.AdminTrialStageDTO;
import eu.fp7.driver.ost.driver.dto.TrialStageDTO;
import eu.fp7.driver.ost.driver.model.TrialStage;
import eu.fp7.driver.ost.driver.service.TrialStageService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/stages")
public class TrialStageController {

    @Autowired
    private TrialStageService trialStageService;


    @FindAllGetMapping
    private PageDto<TrialStageDTO.ListItem> findByTrialId(
            @RequestParam(value = "trial_id") long trialId, Pageable pageable) {
        return Dto.from(trialStageService.findByTrialId(trialId, pageable), TrialStageDTO.ListItem.class);
    }

    @PostMapping("/admin/addNewTrialStage" )
    public ResponseEntity addNewTrialStage(@RequestBody AdminTrialStageDTO.ListItem adminTrialStageDTO) {

        try{
            adminTrialStageDTO.toDto(trialStageService.insert(adminTrialStageDTO));
        }
        catch (Exception e)
        {
            return  getResponseEntity(e);
        }
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(adminTrialStageDTO);
        return responseEntity;
    }

    @GetMapping("/admin/trialStageWithQuestions")
    private ResponseEntity trialStageWithQuestions(@RequestParam(value = "id") long id){


        AdminTrialStageDTO.FullItem adminTrialStageDTO = new AdminTrialStageDTO.FullItem();
        try{
            TrialStage trialStage = trialStageService.findById(id);
            adminTrialStageDTO.toDto(trialStage);
        }
        catch (Exception e)
        {
            return  getResponseEntity(e);
        }
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(adminTrialStageDTO);
        return responseEntity;
    }



    @DeleteMapping("/admin/deleteTrialStage")
    public ResponseEntity deleteTrial(@RequestParam(value = "id") long id) {
        try {
            trialStageService.delete(id);
            ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body("Trial Stage id =" + id + " is deleted");
            return responseEntity;
        }
        catch (Exception e)
        {
            ResponseEntity responseEntity = getResponseEntity(e);
            return responseEntity;
        }

    }

    @PutMapping("/admin/updateTrialStage")
    public ResponseEntity updateTrial(@RequestBody AdminTrialStageDTO.ListItem adminTrialStageDTO) {

        try {
            adminTrialStageDTO.toDto(trialStageService.update(adminTrialStageDTO));
        }
        catch (Exception e)
        {
            ResponseEntity responseEntity = getResponseEntity(e);
            return responseEntity;
        }
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(adminTrialStageDTO);
        return responseEntity;
    }

    private ResponseEntity getResponseEntity(Exception e) {
        JSONObject jsonAnswer = new JSONObject();
        JSONArray serverErrorList = new JSONArray();
        JSONObject serverError = new JSONObject();
        serverError.put("serviceError", "Errors in service." + e.getMessage());
        serverErrorList.put(serverError);
        jsonAnswer.put("status", HttpStatus.BAD_REQUEST);
        jsonAnswer.put("errors", serverErrorList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonAnswer.toString());
    }


}
