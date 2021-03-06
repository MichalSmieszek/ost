package eu.fp7.driver.ost.driver.web;

import eu.fp7.driver.ost.core.annotation.FindAllGetMapping;
import eu.fp7.driver.ost.core.dto.Dto;
import eu.fp7.driver.ost.driver.dto.AdminObservationTypeDTO;
import eu.fp7.driver.ost.driver.dto.ObservationTypeDTO;
import eu.fp7.driver.ost.driver.model.ObservationType;
import eu.fp7.driver.ost.driver.service.ObservationTypeService;
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

import java.util.List;

@RestController
@RequestMapping("/api/observationtypes")
public class ObservationTypeController {

    @Autowired
    private ObservationTypeService observationTypeService;

    @FindAllGetMapping
    public List<ObservationTypeDTO.ListItem> findAll(@RequestParam("trialsession_id") Long trialSessionId, Pageable pageable) {
        return Dto.from(observationTypeService.find(trialSessionId), ObservationTypeDTO.ListItem.class);
    }

    @GetMapping("/form")
    public ObservationTypeDTO.SchemaItem getSchemaForm(@RequestParam("observationtype_id") Long observationTypeId,
                                                       @RequestParam("trialsession_id") Long trialSessionId) {
        return observationTypeService.generateSchema(observationTypeId, trialSessionId);
    }

    @PostMapping("/admin/addNewQuestionSet" )
    public ResponseEntity addNewQuestionSet(@RequestBody AdminObservationTypeDTO.FullItem adminObservationTypeDTO) {

        try{
            adminObservationTypeDTO.toDto(observationTypeService.insert(adminObservationTypeDTO));
        }
        catch (Exception e)
        {
            return  getResponseEntity(e);
        }
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(adminObservationTypeDTO);
        return responseEntity;
    }

    @GetMapping("/admin/getFullQuestionSet")
    private ResponseEntity getFullQuestionSet(@RequestParam(value = "id") long id){


        AdminObservationTypeDTO.FullItem adminQuestionDTO = new AdminObservationTypeDTO.FullItem();
        try{
            ObservationType observationType = observationTypeService.getFullObservationType(id);
            adminQuestionDTO.toDto(observationType);
        }
        catch (Exception e)
        {
            return  getResponseEntity(e);
        }
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(adminQuestionDTO);
        return responseEntity;
    }



    @DeleteMapping("/admin/deleteQuestionSet")
    public ResponseEntity deleteQuestionSet(@RequestParam(value = "id") long id) {
        try {
            observationTypeService.delete(id);
            ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body("Question Set id =" + id + " is deleted");
            return responseEntity;
        }
        catch (Exception e)
        {
            ResponseEntity responseEntity = getResponseEntity(e);
            return responseEntity;
        }

    }

    @PutMapping("/admin/updateQuestionSet")
    public ResponseEntity updateQuestionSet(@RequestBody AdminObservationTypeDTO.FullItem adminObservationTypeDTO) {

        try {
            adminObservationTypeDTO.toDto(observationTypeService.update(adminObservationTypeDTO));
        }
        catch (Exception e)
        {
            ResponseEntity responseEntity = getResponseEntity(e);
            return responseEntity;
        }
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(adminObservationTypeDTO);
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
