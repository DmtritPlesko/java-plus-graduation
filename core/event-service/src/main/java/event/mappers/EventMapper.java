package event.mappers;

import interaction.dto.event.EventFullDto;
import interaction.dto.event.NewEventDto;
import interaction.dto.event.UpdateEventAdminRequest;
import interaction.dto.event.UpdateEventUserRequest;
import category.mapper.CategoryMapper;
import category.model.Category;
import event.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import interaction.dto.event.EventShortDto;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, StateActionMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto, User initiator, Category category);

    @Mapping(target = "confirmedRequests", ignore = true)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "updateEventUserRequest.stateAction")
    Event toUpdatedEvent(@MappingTarget Event event, UpdateEventUserRequest updateEventUserRequest, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "updateEventAdminRequest.stateAction")
    Event toUpdatedEvent(@MappingTarget Event event, UpdateEventAdminRequest updateEventAdminRequest, Category category);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toEventShortDto(Event event);
}
