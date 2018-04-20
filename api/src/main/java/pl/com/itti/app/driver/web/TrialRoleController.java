package pl.com.itti.app.driver.web;

import co.perpixel.dto.DTO;
import co.perpixel.dto.PageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.com.itti.app.driver.dto.TrialRoleDTO;
import pl.com.itti.app.driver.service.TrialRoleService;

@RestController
@RequestMapping("/api/role")
public class TrialRoleController {

    @Autowired
    private TrialRoleService trialRoleService;

    @GetMapping
    private PageDTO<TrialRoleDTO.FullItem> findByTrialSessionId(
            @RequestParam(value = "trialsession_id") long trialSessionId, Pageable pageable) {
        return DTO.from(trialRoleService.findByTrialSessionId(trialSessionId, pageable), TrialRoleDTO.FullItem.class);
    }
}