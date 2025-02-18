package ewm.event.controller;

import ewm.event.dto.AdminEventParam;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.service.AdminEventService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventController {
    final AdminEventService adminEventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllBy(@Validated @ModelAttribute AdminEventParam adminEventParam,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return adminEventService.getAllBy(adminEventParam, PageRequest.of(from, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateBy(@PathVariable("eventId") long eventId,
                                 @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return adminEventService.updateBy(eventId, updateEventAdminRequest);
    }
}
