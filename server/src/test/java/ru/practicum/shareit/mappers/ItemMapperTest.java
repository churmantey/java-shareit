package ru.practicum.shareit.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemMapperTest {

    private final ItemMapperImpl itemMapper;

    @Test
    public void itemMapperTests() {

        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@server.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Roman");
        user2.setEmail("roman@server.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("item #1");
        item.setDescription("item #1 description");
        item.setAvailable(true);
        item.setOwner(user.getId());
        item.setLastBooking(LocalDateTime.now().minusDays(1));
        item.setNextBooking(LocalDateTime.now().plusDays(1));

        Item item2 = new Item();
        item.setId(2L);
        item.setName("item #2");
        item.setDescription("item #2 description");
        item.setAvailable(true);
        item.setOwner(user2.getId());
        item.setLastBooking(LocalDateTime.now().minusDays(1));
        item.setNextBooking(LocalDateTime.now().plusDays(1));

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setAuthor(user);
        comment1.setItem(item);
        comment1.setText("comment #1");
        comment1.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setAuthor(user2);
        comment2.setItem(item);
        comment2.setText("comment #2");
        comment2.setCreated(LocalDateTime.now());

        item.setComments(Arrays.asList(comment1, comment2));

        ItemDto itemDto = itemMapper.itemToDto(item);
        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getOwner(), equalTo(item.getOwner()));
        assertThat(itemDto.getComments().size(), equalTo(item.getComments().size()));

        List<ItemDto> itemDtoList = itemMapper.itemListToDtoList(Arrays.asList(item, item2));

        assertThat(itemDtoList.size(), equalTo(2));
        assertThat(itemDtoList.get(0).getId(), equalTo(item.getId()));
        assertThat(itemDtoList.get(0).getName(), equalTo(item.getName()));
        assertThat(itemDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(itemDtoList.get(1).getName(), equalTo(item2.getName()));

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(3L);
        itemDto1.setName("item #1 dto name");
        itemDto1.setOwner(user.getId());

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(4L);
        itemDto2.setName("item #2 dto name");
        itemDto2.setOwner(user2.getId());

        Item itemFromDto = itemMapper.dtoToItem(itemDto);
        assertThat(itemDto.getId(), equalTo(itemFromDto.getId()));
        assertThat(itemDto.getName(), equalTo(itemFromDto.getName()));
        assertThat(itemDto.getOwner(), equalTo(itemFromDto.getOwner()));
        assertThat(itemDto.getComments().size(), equalTo(itemFromDto.getComments().size()));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(itemFromDto.getComments().get(0).getText()));

        List<Item> itemList = itemMapper.dtoListToItemList(Arrays.asList(itemDto1, itemDto2));
        assertThat(itemList.size(), equalTo(2));
        assertThat(itemList.get(0).getId(), equalTo(itemDto1.getId()));
        assertThat(itemList.get(0).getName(), equalTo(itemDto1.getName()));
        assertThat(itemList.get(0).getOwner(), equalTo(itemDto1.getOwner()));
        assertThat(itemList.get(1).getId(), equalTo(itemDto2.getId()));
        assertThat(itemList.get(1).getName(), equalTo(itemDto2.getName()));
        assertThat(itemList.get(1).getOwner(), equalTo(itemDto2.getOwner()));

        itemDto = itemMapper.itemToDto(null);
        assertThat(itemDto, equalTo(null));

        itemFromDto = itemMapper.dtoToItem(null);
        assertThat(itemFromDto, equalTo(null));

    }

}
