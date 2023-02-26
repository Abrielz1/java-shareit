package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.client.ItemRequestClient;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.Create;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsInfo(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getRequestsInfo(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequestInfo(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsList(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                  @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return itemRequestClient.getRequestsList(userId, from, size);
    }
}