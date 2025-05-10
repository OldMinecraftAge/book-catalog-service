package com.vishnu.bookcatalog.service;

import com.vishnu.bookcatalog.dto.BookRequestDto;
import com.vishnu.bookcatalog.dto.BookResponseDto;

import java.util.List;

public interface BookService {

    BookResponseDto createBook(BookRequestDto dto);

    BookResponseDto getBookById(Long id);

    List<BookResponseDto> getAllBooks();

    BookResponseDto updateBook(Long id, BookRequestDto dto);

    void deleteBook(Long id);
}
