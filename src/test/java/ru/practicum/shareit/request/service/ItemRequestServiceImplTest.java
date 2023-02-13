package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {


    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;


    User user = new User(
            1L,
            "name",
            "email@email.ru");
    ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            1L,
            "description",
            LocalDateTime.now());
    Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);

    @Test
    void create_whenUserFound_thenSaved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto actual = itemRequestService.create(user.getId(), itemRequestDto);
        itemRequestDto.setCreated(actual.getCreated());

        assertEquals(itemRequestDto.getId(), actual.getId());
        verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void create_whenUserNotFound_thenExceptionThrown() {
        when(userRepository.findById(anyLong())).thenThrow(new ObjectNotFoundException("User not found"));

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.create(1L, itemRequestDto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getRequestsInfo_whenUserFound_thenReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        List<ItemRequestDtoResponse> responseList = itemRequestService.getRequestsInfo(user.getId());
        assertTrue(responseList.isEmpty());
        verify(requestRepository).findAllByRequestorId(anyLong());
    }

    @Test
    void getRequestsInfo_whenUserNotFound_thenExceptionThrown() {
        when(userRepository.findById(anyLong())).thenThrow(new ObjectNotFoundException("User not found"));

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getRequestsInfo(1L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getRequestInfo_whenUserAndRequestFound_thenReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        item.setItemRequest(itemRequest);
        when(itemRepository.findByItemRequestId(anyLong())).thenReturn(Collections.singletonList(item));

        ItemRequestDtoResponse responseRequest = itemRequestService.getRequestInfo(user.getId(), itemRequestDto.getId());

        assertNotNull(responseRequest);
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).findByItemRequestId(anyLong());
    }

    @Test
    void getRequestInfo_whenRequestNotFound_thenExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenThrow(new ObjectNotFoundException("Request not found"));

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.getRequestInfo(user.getId(), itemRequestDto.getId()));
        assertEquals("Request not found", ex.getMessage());
    }


}