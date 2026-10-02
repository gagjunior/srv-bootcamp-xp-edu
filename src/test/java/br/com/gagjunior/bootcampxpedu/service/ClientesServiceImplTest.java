package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.DuplicateResourceException;
import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Clientes;
import br.com.gagjunior.bootcampxpedu.repository.ClientesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para ClientesServiceImpl")
class ClientesServiceImplTest {

    @Mock
    private ClientesRepository clientesRepository;

    private ClientesServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClientesServiceImpl(clientesRepository);
    }

    @Test
    @DisplayName("não permite repository nulo")
    void shouldRejectNullRepository() {
        assertThatNullPointerException().isThrownBy(() -> new ClientesServiceImpl(null))
                .withMessage("clientesRepository não pode ser nulo");
    }

    @Test
    void shouldFindAllClients() {
        List<Clientes> clients = List.of(client("1", "123"));
        when(clientesRepository.findAll()).thenReturn(clients);

        assertThat(service.findAll()).isEqualTo(clients);
        verify(clientesRepository).findAll();
    }

    @Test
    void shouldPropagateRepositoryFailureWhenFindingAllClients() {
        RuntimeException failure = new RuntimeException("database unavailable");
        when(clientesRepository.findAll()).thenThrow(failure);

        assertThatThrownBy(() -> service.findAll()).isSameAs(failure);
    }

    @Test
    void shouldFindClientById() {
        Clientes expected = client("1", "123");
        when(clientesRepository.findById("1")).thenReturn(Optional.of(expected));

        assertThat(service.findById("1")).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenClientIdDoesNotExist() {
        when(clientesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Cliente não encontrado: missing");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectInvalidClientId(String id) {
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(clientesRepository);
    }

    @Test
    void shouldFindClientByCpf() {
        Clientes expected = client("1", "123");
        when(clientesRepository.findByCpf("123")).thenReturn(Optional.of(expected));

        assertThat(service.findByCpf("123")).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenCpfDoesNotExist() {
        when(clientesRepository.findByCpf("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByCpf("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Cliente com CPF não encontrado: 999");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidCpf(String cpf) {
        assertThatThrownBy(() -> service.findByCpf(cpf))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(clientesRepository);
    }

    @Test
    void shouldFindClientByEmail() {
        Clientes expected = client("1", "123");
        when(clientesRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(expected));

        assertThat(service.findByEmail("ana@example.com")).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenEmailDoesNotExist() {
        when(clientesRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByEmail("missing@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Cliente com e-mail não encontrado: missing@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidEmail(String email) {
        assertThatThrownBy(() -> service.findByEmail(email))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(clientesRepository);
    }

    @Test
    void shouldSaveClientWhenCpfAndEmailAreAvailable() {
        Clientes newClient = client(null, "123");
        when(clientesRepository.existsByCpf("123")).thenReturn(false);
        when(clientesRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(clientesRepository.save(newClient)).thenReturn(newClient);

        assertThat(service.save(newClient)).isSameAs(newClient);
        verify(clientesRepository).existsByCpf("123");
        verify(clientesRepository).existsByEmail("ana@example.com");
        verify(clientesRepository).save(newClient);
    }

    @Test
    void shouldRejectNullClientOnSave() {
        assertThatNullPointerException().isThrownBy(() -> service.save(null))
                .withMessage("cliente não pode ser nulo");
        verifyNoInteractions(clientesRepository);
    }

    @Test
    void shouldRejectDuplicatedCpfOnCreate() {
        Clientes newClient = client(null, "123");
        when(clientesRepository.existsByCpf("123")).thenReturn(true);

        assertThatThrownBy(() -> service.save(newClient))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Já existe um cliente cadastrado com o CPF informado");
        verify(clientesRepository, never()).existsByEmail(anyString());
        verify(clientesRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicatedEmailOnCreate() {
        Clientes newClient = client(null, "123");
        when(clientesRepository.existsByCpf("123")).thenReturn(false);
        when(clientesRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.save(newClient))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Já existe um cliente cadastrado com o e-mail informado");
        verify(clientesRepository, never()).save(any());
    }

    @Test
    void shouldRejectInvalidCpfAndEmailFromClientOnSave() {
        Clientes invalid = mock(Clientes.class);
        when(invalid.cpf()).thenReturn(" ");

        assertThatIllegalArgumentException().isThrownBy(() -> service.save(invalid))
                .withMessage("CPF não pode ser vazio");

        Clientes nullEmail = mock(Clientes.class);
        when(nullEmail.cpf()).thenReturn("123");
        when(nullEmail.email()).thenReturn(null);
        when(clientesRepository.existsByCpf("123")).thenReturn(false);
        assertThatNullPointerException().isThrownBy(() -> service.save(nullEmail))
                .withMessage("e-mail não pode ser nulo");
    }

    @Test
    void shouldUpdateClientAndKeepItsId() {
        Clientes existing = client("1", "123");
        Clientes changes = new Clientes(null, "Ana Updated", "456", "updated@example.com");
        Clientes saved = new Clientes("1", "Ana Updated", "456", "updated@example.com");
        when(clientesRepository.findById("1")).thenReturn(Optional.of(existing));
        when(clientesRepository.findByCpf("456")).thenReturn(Optional.empty());
        when(clientesRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(clientesRepository.save(saved)).thenReturn(saved);

        assertThat(service.update("1", changes)).isEqualTo(saved);
        verify(clientesRepository).save(saved);
    }

    @Test
    void shouldAllowUpdatingWithTheSameCpfAndEmail() {
        Clientes existing = client("1", "123");
        when(clientesRepository.findById("1")).thenReturn(Optional.of(existing));
        when(clientesRepository.findByCpf("123")).thenReturn(Optional.of(existing));
        when(clientesRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(existing));
        when(clientesRepository.save(any(Clientes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.update("1", client(null, "123"))).isEqualTo(client("1", "Ana", "123", "ana@example.com"));
    }

    @Test
    void shouldRejectDuplicatedCpfOnUpdate() {
        Clientes existing = client("1", "123");
        Clientes another = client("2", "456");
        when(clientesRepository.findById("1")).thenReturn(Optional.of(existing));
        when(clientesRepository.findByCpf("456")).thenReturn(Optional.of(another));

        assertThatThrownBy(() -> service.update("1", client(null, "456")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(clientesRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicatedEmailOnUpdate() {
        Clientes existing = client("1", "123");
        Clientes another = client("2", "456");
        when(clientesRepository.findById("1")).thenReturn(Optional.of(existing));
        when(clientesRepository.findByCpf("456")).thenReturn(Optional.empty());
        when(clientesRepository.findByEmail("other@example.com")).thenReturn(Optional.of(another));

        assertThatThrownBy(() -> service.update("1", new Clientes(null, "Ana", "456", "other@example.com")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(clientesRepository, never()).save(any());
    }

    @Test
    void shouldRejectNullClientOnUpdate() {
        assertThatNullPointerException().isThrownBy(() -> service.update("1", null))
                .withMessage("cliente não pode ser nulo");
        verifyNoInteractions(clientesRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidIdOnUpdateAndDelete(String id) {
        assertThatThrownBy(() -> service.update(id, client(null, "123")))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(clientesRepository);
    }

    @Test
    void shouldDeleteClientWhenItExists() {
        Clientes existing = client("1", "123");
        when(clientesRepository.findById("1")).thenReturn(Optional.of(existing));

        service.deleteById("1");

        verify(clientesRepository).deleteById("1");
    }

    @Test
    void shouldNotDeleteMissingClient() {
        when(clientesRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(clientesRepository, never()).deleteById(anyString());
    }

    @Test
    void shouldNotConsiderResourceWithNullCurrentIdAsTheSameResource() throws Exception {
        Method method = ClientesServiceImpl.class.getDeclaredMethod("isSameResource", String.class, String.class);
        method.setAccessible(true);

        assertThat(method.invoke(service, "1", null)).isEqualTo(false);
    }

    private static Clientes client(String id, String cpf) {
        return client(id, "Ana", cpf, "ana@example.com");
    }

    private static Clientes client(String id, String name, String cpf, String email) {
        return new Clientes(id, name, cpf, email);
    }
}
