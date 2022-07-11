package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Singleton
public class DocumentServicesImpl implements DocumentServices {

    private final DocumentRepository documentRepository;

    public DocumentServicesImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document save(@NotNull Document document) {
        if (document.getId() != null) {
            throw new BadArgException("Cannot create document with id present");
        } else if (document.getName().isBlank()) {
            throw new BadArgException("Document name must not be blank");
        } else if (documentRepository.findByName(document.getName()).isPresent()) {
            throw new AlreadyExistsException(String.format("A document with the name '%s' already exists", document.getName()));
        }

        return documentRepository.save(document);
    }

    @Override
    public Document getById(@NotNull UUID id) {
        return documentRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("Document with id %s not found", id));
        });
    }

    @Override
    public Document update(@NotNull Document document) {
        if (document.getId() == null) {
            throw new BadArgException("Cannot update document without id present");
        } else if (documentRepository.findById(document.getId()).isEmpty()) {
            throw new BadArgException(String.format("Cannot find document to update with id %s", document.getId()));
        } else if (document.getName().isBlank()) {
            throw new BadArgException("Document name must not be blank");
        } else if (documentRepository.findByName(document.getName()).isPresent()) {
            throw new AlreadyExistsException(String.format("A document with the name '%s' already exists", document.getName()));
        }

        return documentRepository.update(document);
    }

    @Override
    public void delete(@NotNull UUID id) {
        documentRepository.deleteById(id);
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }
}
