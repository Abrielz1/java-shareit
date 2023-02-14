package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import static org.mockito.ArgumentMatchers.anyLong;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import org.mockito.InjectMocks;
import java.util.Collections;
import org.mockito.Mockito;
import java.util.Optional;
import org.mockito.Mock;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {


    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;


    private User user = new User(
            1L,
            "name",
            "email@email.ru");
    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            1L,
            "description",
            LocalDateTime.now());
    private Item item = new Item(
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

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.create(1L, itemRequestDto));
        assertEquals("User not found", exception.getMessage());
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

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getRequestsInfo(1L));
        assertEquals("User not found", exception.getMessage());
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

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.getRequestInfo(user.getId(), itemRequestDto.getId()));
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void getRequestsListTest() { //todo Почему-то не засчитывается
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenThrow(new ObjectNotFoundException("Запрос не найден"));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                itemRequestService.getRequestInfo(user.getId(), 1L)
        );

        assertEquals("Запрос не найден", exception.getMessage());
    }
}