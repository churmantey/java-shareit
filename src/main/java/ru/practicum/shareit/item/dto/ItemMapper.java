package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    public Item dtoToItem (ItemDto itemDto);

    public ItemDto itemToDto (Item item);

    public List<Item> dtoListToItemList (List<ItemDto> itemDto);

    public List<ItemDto> itemListToDtoList (List<Item> itemDto);

}
