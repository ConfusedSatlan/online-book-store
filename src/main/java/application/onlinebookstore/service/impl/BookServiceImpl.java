package application.onlinebookstore.service.impl;

import application.onlinebookstore.dto.BookDto;
import application.onlinebookstore.dto.BookSearchParametersDto;
import application.onlinebookstore.dto.CreateBookRequestDto;
import application.onlinebookstore.exception.EntityNotFoundException;
import application.onlinebookstore.mapper.BookMapper;
import application.onlinebookstore.model.Book;
import application.onlinebookstore.repository.book.BookRepository;
import application.onlinebookstore.repository.book.impl.BookSpecificationBuilder;
import application.onlinebookstore.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookDto) {
        Book savedBook = bookRepository.save(bookMapper.toModel(bookDto));
        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find book by id: " + id)
                ));
    }

    @Override
    public List<BookDto> getBookByTitle(String title) {
        List<Book> allByTitleContainsIgnoreCase = bookRepository
                .findAllByTitleContainsIgnoreCase(title);
        if (allByTitleContainsIgnoreCase.isEmpty()) {
            throw new EntityNotFoundException("Can't find book by title: " + title);
        }
        return allByTitleContainsIgnoreCase.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> getBookByAuthor(String author) {
        List<Book> allByAuthorContainsIgnoreCase = bookRepository
                .findAllByAuthorContainsIgnoreCase(author);
        if (allByAuthorContainsIgnoreCase.isEmpty()) {
            throw new EntityNotFoundException("Can't find book by author: " + author);
        }
        return allByAuthorContainsIgnoreCase.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParameters) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto bookDto) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Book with id: " + id + " not found.");
        }
        Book updateBook = bookMapper.toModel(bookDto);
        updateBook.setId(id);
        return bookMapper.toDto(bookRepository.save(updateBook));
    }
}
