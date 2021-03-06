package eu.fp7.driver.ost.core.security.security.web.rest;

import eu.fp7.driver.ost.core.annotation.DeleteMapping;
import eu.fp7.driver.ost.core.annotation.FindAllGetMapping;
import eu.fp7.driver.ost.core.annotation.FindOneGetMapping;
import eu.fp7.driver.ost.core.annotation.IsAdmin;
import eu.fp7.driver.ost.core.annotation.PostMapping;
import eu.fp7.driver.ost.core.annotation.PutMapping;
import eu.fp7.driver.ost.core.dto.Dto;
import eu.fp7.driver.ost.core.dto.PageDto;
import eu.fp7.driver.ost.core.exception.FormValidationException;
import eu.fp7.driver.ost.core.security.security.service.AuthUserService;
import eu.fp7.driver.ost.core.security.security.web.dto.AuthUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@IsAdmin
@Controller
@RequestMapping("/api/auth/users")
public class AuthUserController {

    @Autowired
    private AuthUserService service;

    @FindAllGetMapping
    public PageDto<AuthUserDto.ListItem> findAll(@SortDefault(sort = "last_name", direction = Sort.Direction.ASC) Pageable pageable) {
        // TODO MetaModel in SortDefault
        return Dto.from(service.findAll(pageable), AuthUserDto.ListItem.class);
    }

    @FindOneGetMapping
    public AuthUserDto.FullItem findOne(@PathVariable("id") long id) {
        return Dto.from(service.findOne(id), AuthUserDto.FullItem.class);
    }

    @PostMapping
    public AuthUserDto.FullItem create(@Validated @RequestBody AuthUserDto.CreateFormItem form,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new FormValidationException(AuthUserDto.CreateFormItem.class);
        }

        return Dto.from(service.create(form), AuthUserDto.FullItem.class);
    }

    @PutMapping
    public AuthUserDto.FullItem update(@PathVariable("id") long id,
                                       @Validated @RequestBody AuthUserDto.UpdateFormItem form,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new FormValidationException(AuthUserDto.UpdateFormItem.class);
        }

        return Dto.from(service.update(id, form), AuthUserDto.FullItem.class);
    }

    @PutMapping(value = "/{id:\\d+}/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable("id") long id,
                               @Validated @RequestBody AuthUserDto.ChangePasswordFormItem form,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new FormValidationException(AuthUserDto.ChangePasswordFormItem.class);
        }

        service.changePassword(id, form.password);
    }

    @DeleteMapping
    public void delete(@PathVariable("id") long id) {
        service.delete(id);
    }

    @RequestMapping(value = "/validate-login", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AuthUserDto.ValidateLoginResponse validateLogin(@RequestParam("login") String login) {
        Pair<Boolean, Boolean> validationResult = service.validateLogin(login);
        AuthUserDto.ValidateLoginResponse response = new AuthUserDto.ValidateLoginResponse();
        response.valid = validationResult.getFirst();
        response.unique = validationResult.getSecond();
        return response;
    }
}
