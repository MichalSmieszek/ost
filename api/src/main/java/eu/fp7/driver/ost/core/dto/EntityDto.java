package eu.fp7.driver.ost.core.dto;

public interface EntityDto<T_Entity> {

    /**
     * Converts Entity into Data Transfer Object
     *
     * @param entity Entity to be converted
     */
    void toDto(T_Entity entity);
}
