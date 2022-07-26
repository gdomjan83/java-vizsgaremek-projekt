package invoicekeeper.service;

import invoicekeeper.dtos.*;
import invoicekeeper.exceptions.*;
import invoicekeeper.model.Company;
import invoicekeeper.model.Invoice;
import invoicekeeper.model.PaymentStatus;
import invoicekeeper.repositories.CompanyRepository;
import invoicekeeper.repositories.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoicingService {
    private InvoiceRepository invoiceRepository;
    private CompanyRepository companyRepository;
    private ModelMapper modelMapper;

    @Transactional
    public InvoiceDto saveNewInvoice(CreateNewInvoiceCommand command) {
        checkIfInvoiceAlreadyExists(command.getInvoiceNumber());
        Invoice newInvoice = checkForExistingCompanyThenSave(command);
        InvoiceDto resultDto = modelMapper.map(newInvoice, InvoiceDto.class);
        resultDto.setCompanyName(newInvoice.getCompany().getCompanyName());
        return resultDto;
    }

    @Transactional
    public InvoiceDto payInvoice(PayInvoiceCommand command) {
        Invoice invoiceFound = invoiceRepository.findByInvoiceNumber(command.getInvoiceNumber())
                .orElseThrow(() -> new InvoiceNotFoundException(command.getInvoiceNumber()));
        validatePaymentData(invoiceFound, command);
        invoiceFound.setPaymentStatus(PaymentStatus.PAYED);
        InvoiceDto resultDto = modelMapper.map(invoiceFound, InvoiceDto.class);
        resultDto.setCompanyName(invoiceFound.getCompany().getCompanyName());
        return resultDto;
    }

    public InvoiceDto getInvoiceById(long id) {
        Invoice invoiceFound = findInvoiceById(id);
        return modelMapper.map(invoiceFound, InvoiceDto.class);
    }

    public List<InvoiceDto> getAllInvoices(Optional<String> companyName, Optional<String> vatNumber,
                                           Optional<LocalDate> issuedAfter, Optional<String> overDue) {
        List<Invoice> filtered = invoiceRepository.findInvoicesByParameters(companyName, vatNumber, issuedAfter);
        if (overDue.isPresent()) {
            filtered = filterOverdueInvoices(filtered, overDue.get());
        }
        Type resultList = new TypeToken<List<InvoiceDto>>(){}.getType();
        return modelMapper.map(filtered, resultList);
    }

    public List<InvoiceDto> getInvoicesByItemName(String name) {
        List<Invoice> invoices = invoiceRepository.findInvoiceByItemName(name);
        Type resultList = new TypeToken<List<InvoiceDto>>(){}.getType();
        return modelMapper.map(invoices, resultList);
    }

    @Transactional
    public boolean deleteInvoiceById(long id) {
        invoiceRepository.deleteById(id);
        return true;
    }

    //COMPANY METHODS

    @Transactional
    public CompanyDto addNewCompany(AddNewCompanyCommand command) {
        checkIfCompanyAlreadyExists(command.getVatNumber());
        Company newCompany = modelMapper.map(command, Company.class);
        companyRepository.save(newCompany);
        return modelMapper.map(newCompany, CompanyDto.class);
    }

    @Transactional
    public CompanyDto addNewInvoiceToCompany(long id, AddNewInvoiceCommand command) {
        checkIfInvoiceAlreadyExists(command.getInvoiceNumber());
        Invoice newInvoice = modelMapper.map(command, Invoice.class);
        Company companyFound = findCompanyById(id);
        newInvoice.setCompany(companyFound);
        invoiceRepository.save(newInvoice);
        CompanyDto resultDto = modelMapper.map(companyFound, CompanyDto.class);
        resultDto.setCompanyName(companyFound.getCompanyName());
        return resultDto;
    }

    @Transactional
    public CompanyDto updateAccountNumber(long id, UpdateAccountNumberCommand command) {
        Company companyFound = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        companyFound.setBankAccountNumber(command.getBankAccountNumber());
        return modelMapper.map(companyFound, CompanyDto.class);
    }

    public CompanyDto getCompanyById(long id) {
        return modelMapper.map(findCompanyById(id), CompanyDto.class);
    }

    public CompanyDto getCompanyByVatNumber(String vatNumber) {
        Company companyFound = companyRepository.findCompanyByVatNumber(vatNumber)
                .orElseThrow(() -> new CompanyNotFoundException(vatNumber));
        return modelMapper.map(companyFound, CompanyDto.class);
    }

    public List<CompanyDto> findAllCompanies(Optional<String> searchName) {
        List<Company> companiesFound = companyRepository.findAllCompanies(searchName);
        Type resultList = new TypeToken<List<CompanyDto>>(){}.getType();
        return modelMapper.map(companiesFound, resultList);
    }

    @Transactional
    public boolean deleteCompanyById(long id) {
        Company companyFound = findCompanyById(id);
        companyRepository.delete(companyFound);
        return true;
    }

    private Invoice checkForExistingCompanyThenSave(CreateNewInvoiceCommand command) {
        Optional<Company> companyFound = companyRepository.findCompanyByVatNumber(command.getVatNumber());
        Invoice newInvoice = modelMapper.map(command, Invoice.class);
        if (companyFound.isPresent()) {
            newInvoice.setCompany(companyFound.get());
            invoiceRepository.save(newInvoice);
        } else {
            Company newCompany = new Company(command.getCompanyName(), command.getVatNumber(), command.getBankAccountNumber());
            newCompany.addInvoice(newInvoice);
            companyRepository.save(newCompany);
        }
        return newInvoice;
    }

    private List<Invoice> filterOverdueInvoices(List<Invoice> invoices, String filterBy) {
        if ("yes".equals(filterBy)) {
            return invoices.stream()
                    .filter(i -> i.getDueDate().isBefore(LocalDate.now()))
                    .collect(Collectors.toList());
        } else if ("no".equals(filterBy)) {
            return invoices.stream()
                    .filter(i -> !i.getDueDate().isBefore(LocalDate.now()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(invoices);
        }
    }

    private Invoice findInvoiceById(long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
    }

    private Company findCompanyById(long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
    }

    private void checkIfCompanyAlreadyExists(String vatNumber) {
        if (companyRepository.findCompanyByVatNumber(vatNumber).isPresent()) {
            throw new CompanyWithVatNumberAlreadyExistsException(vatNumber);
        }
    }

    private void checkIfInvoiceAlreadyExists(String invoiceNumber) {
        if (invoiceRepository.findByInvoiceNumber(invoiceNumber).isPresent()) {
            throw new InvoiceWithNumberAlreadyExistsException(invoiceNumber);
        }
    }

    private void validatePaymentData(Invoice invoice, PayInvoiceCommand command) {
        if (command.getAmount() > invoice.getAmount()) {
            throw new IncorrectPaymentException(invoice.getAmount());
        }
        if (!command.getBankAccountNumber().equals(invoice.getCompany().getBankAccountNumber())) {
            throw new IncorrectPaymentException(command.getBankAccountNumber());
        }
    }
}
