package com.vishnu.bookcatalog.service.impl;

import com.vishnu.bookcatalog.dto.BookRequestDto;
import com.vishnu.bookcatalog.dto.BookResponseDto;
import com.vishnu.bookcatalog.entity.Book;
import com.vishnu.bookcatalog.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequestDto requestDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("9780134685991");
        book.setGenre("Programming");
        book.setPublishedDate(LocalDate.of(2018, 1, 6));
        book.setSummary("Best practices for Java programming.");

        requestDto = new BookRequestDto();
        requestDto.setTitle("Effective Java");
        requestDto.setAuthor("Joshua Bloch");
        requestDto.setIsbn("9780134685991");
        requestDto.setGenre("Programming");
        requestDto.setPublishedDate(LocalDate.of(2018, 1, 6));
        requestDto.setSummary("Best practices for Java programming.");
    }

    @Test
    void createBook_Success() {
        when(bookRepository.existsByIsbn(requestDto.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDto response = bookService.createBook(requestDto);

        assertNotNull(response);
        assertEquals(book.getId(), response.getId());
        assertEquals(book.getTitle(), response.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_DuplicateIsbn_Throws() {
        when(bookRepository.existsByIsbn(requestDto.getIsbn())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(requestDto));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponseDto response = bookService.getBookById(1L);

        assertEquals(book.getId(), response.getId());
        assertEquals(book.getTitle(), response.getTitle());
    }

    @Test
    void getBookById_NotFound_Throws() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void getAllBooks_ReturnsList() {
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));

        List<BookResponseDto> books = bookService.getAllBooks();

        assertEquals(1, books.size());
        assertEquals(book.getTitle(), books.getFirst().getTitle());
    }

    @Test
    void getAllBooks_EmptyList() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<BookResponseDto> books = bookService.getAllBooks();

        assertTrue(books.isEmpty());
    }

    @Test
    void updateBook_Success() {
        Book updated = new Book();
        updated.setId(1L);
        updated.setTitle("Effective Java 3rd Edition");
        updated.setAuthor(book.getAuthor());
        updated.setIsbn(book.getIsbn());
        updated.setGenre(book.getGenre());
        updated.setPublishedDate(book.getPublishedDate());
        updated.setSummary(book.getSummary());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updated);

        requestDto.setTitle("Effective Java 3rd Edition");

        BookResponseDto response = bookService.updateBook(1L, requestDto);

        assertEquals(updated.getTitle(), response.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_NotFound_Throws() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.updateBook(1L, requestDto));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_NotFound_Throws() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).deleteById(anyLong());
    }
}
