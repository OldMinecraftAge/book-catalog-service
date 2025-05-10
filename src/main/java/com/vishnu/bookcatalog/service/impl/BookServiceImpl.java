package com.vishnu.bookcatalog.service.impl;

import com.vishnu.bookcatalog.dto.BookRequestDto;
import com.vishnu.bookcatalog.dto.BookResponseDto;
import com.vishnu.bookcatalog.entity.Book;
import com.vishnu.bookcatalog.repository.BookRepository;
import com.vishnu.bookcatalog.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponseDto createBook(BookRequestDto dto) {
        log.debug("Creating book with ISBN: {}", dto.getIsbn());
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            log.warn("Duplicate ISBN detected: {}", dto.getIsbn());
            throw new IllegalArgumentException("Book with ISBN already exists");
        }
        Book book = mapToEntity(dto);
        Book saved = bookRepository.save(book);
        log.info("Book created with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        log.debug("Fetching book by ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found with ID: {}", id);
                    return new NoSuchElementException("Book not found");
                });
        return mapToDto(book);
    }

    @Override
    public List<BookResponseDto> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto dto) {
        log.debug("Updating book ID: {}", id);
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found for update, ID: {}", id);
                    return new NoSuchElementException("Book not found");
                });
        existing.setTitle(dto.getTitle());
        existing.setAuthor(dto.getAuthor());
        existing.setIsbn(dto.getIsbn());
        existing.setGenre(dto.getGenre());
        existing.setPublishedDate(dto.getPublishedDate());
        existing.setSummary(dto.getSummary());
        Book updated = bookRepository.save(existing);
        log.info("Book updated ID: {}", updated.getId());
        return mapToDto(updated);
    }

    @Override
    public void deleteBook(Long id) {
        log.debug("Deleting book ID: {}", id);
        if (!bookRepository.existsById(id)) {
            log.warn("Book not found for deletion, ID: {}", id);
            throw new NoSuchElementException("Book not found");
        }
        bookRepository.deleteById(id);
        log.info("Book deleted ID: {}", id);
    }

    private BookResponseDto mapToDto(Book book) {
        BookResponseDto dto = new BookResponseDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setGenre(book.getGenre());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setSummary(book.getSummary());
        return dto;
    }

    private Book mapToEntity(BookRequestDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setGenre(dto.getGenre());
        book.setPublishedDate(dto.getPublishedDate());
        book.setSummary(dto.getSummary());
        return book;
    }
}