package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import static ru.practicum.shareit.ShareItApp.HEADER;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import javax.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.constraints.Positive;
import ru.practicum.shareit.user.Create;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {


    private final ItemService itemService;

        @GetMapping
    public List<ItemDtoBooking> findAll(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size,
            @RequestHeader(HEADER) long id) {
        return itemService.findAll(id, from, size);
    }


    @GetMapping("/{itemId}")
    public ItemDtoBooking findItem(@RequestHeader(HEADER) long userId, @PathVariable long itemId) {
        return itemService.findItem(userId, itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER) long userId, @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                    @Positive @RequestParam(required = false, defaultValue = "10") int size,
                                    @RequestParam String text) {
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER) long userId,
                                 @PathVariable long itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
