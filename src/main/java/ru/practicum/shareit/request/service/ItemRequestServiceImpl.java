package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;

    private final ItemRequestRepository requestRepository;

    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        log.info("Запрос создан с id {}", itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoResponse> getRequestsInfo(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        List<ItemRequestDtoResponse> responseList = requestRepository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());
        return setItemsToRequests(responseList);
    }

    @Override
    public ItemRequestDtoResponse getRequestInfo(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException("Запрос не найден"));
        List<ItemDto> items = itemRepository.findByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        itemRequestDtoResponse.setItems(items);
        return itemRequestDtoResponse;
    }

    @Override
    public List<ItemRequestDtoResponse> getRequestsList(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequestDtoResponse> responseList = requestRepository.findAllPageable(userId, pageRequest).stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());
        return setItemsToRequests(responseList);
    }

    private List<ItemRequestDtoResponse> setItemsToRequests(List<ItemRequestDtoResponse> itemRequestDtoResponseList) {
        Map<Long, ItemRequestDtoResponse> requests = itemRequestDtoResponseList.stream()
                .collect(Collectors.toMap(ItemRequestDtoResponse::getId, film -> film, (a, b) -> b));
        List<Long> ids = requests.values().stream()
                .map(ItemRequestDtoResponse::getId)
                .collect(Collectors.toList());
        List<ItemDto> items = itemRepository.searchByRequestsId(ids).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        items.forEach(itemDto -> requests.get(itemDto.getRequestId()).getItems().add(itemDto));
        return new ArrayList<>(requests.values());
    }
}
