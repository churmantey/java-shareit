package ru.practicum.shareit.mappers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapperTest {

    private final ItemRequestMapperImpl requestMapper;

    @Test
    public void itemRequestMapperTests() {

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("request #1 description");
        request.setAuthor(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("request #2 description");
        request2.setAuthor(2L);

        ItemRequestDto requestDto = requestMapper.itemRequestToDto(request);
        assertThat(requestDto.getId(), equalTo(request.getId()));
        assertThat(requestDto.getAuthor(), equalTo(request.getAuthor()));
        assertThat(requestDto.getDescription(), equalTo(request.getDescription()));

        List<ItemRequestDto> requestDtoList = requestMapper.itemRequestListToDtoList(
                Arrays.asList(request, request2));

        assertThat(requestDtoList.size(), equalTo(2));
        assertThat(requestDtoList.get(0).getId(), equalTo(request.getId()));
        assertThat(requestDtoList.get(0).getAuthor(), equalTo(request.getAuthor()));
        assertThat(requestDtoList.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(requestDtoList.get(1).getId(), equalTo(request2.getId()));
        assertThat(requestDtoList.get(1).getAuthor(), equalTo(request2.getAuthor()));
        assertThat(requestDtoList.get(1).getDescription(), equalTo(request2.getDescription()));

        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setId(3L);
        requestDto1.setDescription("dto #1 description");
        requestDto1.setAuthor(1L);

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setId(4L);
        requestDto2.setDescription("dto #2 description");
        requestDto2.setAuthor(1L);

        ItemRequest requestFromDto = requestMapper.dtoToItemRequest(requestDto1);
        assertThat(requestDto1.getId(), equalTo(requestFromDto.getId()));
        assertThat(requestDto1.getAuthor(), equalTo(requestFromDto.getAuthor()));
        assertThat(requestDto1.getDescription(), equalTo(requestFromDto.getDescription()));

        List<ItemRequest> requestList = requestMapper.dtoListToItemRequestList(
                Arrays.asList(requestDto1, requestDto2));
        assertThat(requestList.size(), equalTo(2));
        assertThat(requestList.get(0).getId(), equalTo(requestDto1.getId()));
        assertThat(requestList.get(0).getAuthor(), equalTo(requestDto1.getAuthor()));
        assertThat(requestList.get(0).getDescription(), equalTo(requestDto1.getDescription()));
        assertThat(requestList.get(1).getId(), equalTo(requestDto2.getId()));
        assertThat(requestList.get(1).getAuthor(), equalTo(requestDto2.getAuthor()));
        assertThat(requestList.get(1).getDescription(), equalTo(requestDto2.getDescription()));

        requestDto = requestMapper.itemRequestToDto(null);
        assertThat(requestDto, equalTo(null));

        requestFromDto = requestMapper.dtoToItemRequest(null);
        assertThat(requestFromDto, equalTo(null));
    }
}
