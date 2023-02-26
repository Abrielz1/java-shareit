package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> getRequestsInfo(long userId);

    ItemRequestDtoResponse getRequestInfo(long userId, long requestId);

    List<ItemRequestDtoResponse> getRequestsList(long userId, int from, int size);
}