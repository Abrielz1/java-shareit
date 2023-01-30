package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.item.mapper.ItemMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    private final UserStorage userStorage;

    @Transactional
    @Override
    public ItemDto create(long owner, ItemDto itemDto) {
        userStorage.findById(owner).orElseThrow(() -> {
            log.warn("Пользователь c id{} не найден", owner);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Вещь с id{} создана", itemDto.getId());
        Item item = itemStorage.save(ItemMapper.toItem(itemDto, owner));
        itemDto.setId(item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long owner, long itemId, ItemDto itemDto) {
        Item item = itemStorage.findById(owner).orElseThrow(() -> {
            log.warn("Вещь с id{} для обновления не найдена", itemId);
            throw new ObjectNotFoundException("Вещь для обновления не найдена");
        });
        if (item.getOwner() == owner) {
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
            itemStorage.save(item);
            log.info("Вещь c id{} обновлена", itemId);
        } else {
            log.warn("Вещь с id{} для обновления не найдена", itemId);
            throw new ObjectNotFoundException("Вещь для обновления не найдена");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findItem(long itemId) {
        log.info("Вещь c id{} отправлена", itemId);
        Item item = itemStorage.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь c id{} не найдена", itemId);
            throw new ObjectNotFoundException("Вещь не найдена");
        });
        return ItemMapper.toItemDto(item);
    }
    @Override
    public List<ItemDto> findAll(long owner) {
        log.info("Список вещей владельца с id{} отправлена", owner);
        return itemStorage.findAllByOwner(owner).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Высланы результаты поиска");
        if (text.isBlank()) {
            log.info("Ничего не найдено!");
            return Collections.emptyList();
        }
        return itemStorage.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
