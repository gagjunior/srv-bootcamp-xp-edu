package br.com.gagjunior.bootcampxpedu.service;

import br.com.gagjunior.bootcampxpedu.exception.DuplicateResourceException;
import br.com.gagjunior.bootcampxpedu.exception.ResourceNotFoundException;
import br.com.gagjunior.bootcampxpedu.model.Produtos;
import br.com.gagjunior.bootcampxpedu.repository.ProdutosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para ProdutosServiceImpl")
class ProdutosServiceImplTest {

    @Mock
    private ProdutosRepository produtosRepository;

    private ProdutosServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProdutosServiceImpl(produtosRepository);
    }

    @Test
    @DisplayName("não permite repository nulo")
    void shouldRejectNullRepository() {
        assertThatNullPointerException().isThrownBy(() -> new ProdutosServiceImpl(null))
                .withMessage("produtosRepository não pode ser nulo");
    }

    @Test
    void shouldFindAllProducts() {
        List<Produtos> products = List.of(product("1", "SKU-1"));
        when(produtosRepository.findAll()).thenReturn(products);

        assertThat(service.findAll()).isEqualTo(products);
    }

    @Test
    void shouldPropagateRepositoryFailureWhenFindingAllProducts() {
        RuntimeException failure = new RuntimeException("database unavailable");
        when(produtosRepository.findAll()).thenThrow(failure);

        assertThatThrownBy(() -> service.findAll()).isSameAs(failure);
    }

    @Test
    void shouldFindProductById() {
        Produtos expected = product("1", "SKU-1");
        when(produtosRepository.findById("1")).thenReturn(Optional.of(expected));

        assertThat(service.findById("1")).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenProductIdDoesNotExist() {
        when(produtosRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Produto não encontrado: missing");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void shouldRejectInvalidProductId(String id) {
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(produtosRepository);
    }

    @Test
    void shouldFindProductByCode() {
        Produtos expected = product("1", "SKU-1");
        when(produtosRepository.findByCodigo("SKU-1")).thenReturn(Optional.of(expected));

        assertThat(service.findByCodigo("SKU-1")).isSameAs(expected);
    }

    @Test
    void shouldThrowWhenProductCodeDoesNotExist() {
        when(produtosRepository.findByCodigo("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByCodigo("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Recurso Produto com código não encontrado: missing");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidProductCode(String code) {
        assertThatThrownBy(() -> service.findByCodigo(code))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(produtosRepository);
    }

    @Test
    void shouldSaveProductWhenCodeIsAvailable() {
        Produtos newProduct = product(null, "SKU-1");
        when(produtosRepository.existsByCodigo("SKU-1")).thenReturn(false);
        when(produtosRepository.save(newProduct)).thenReturn(newProduct);

        assertThat(service.save(newProduct)).isSameAs(newProduct);
        verify(produtosRepository).existsByCodigo("SKU-1");
        verify(produtosRepository).save(newProduct);
    }

    @Test
    void shouldRejectNullProductOnSave() {
        assertThatNullPointerException().isThrownBy(() -> service.save(null))
                .withMessage("produto não pode ser nulo");
        verifyNoInteractions(produtosRepository);
    }

    @Test
    void shouldRejectDuplicatedCodeOnCreate() {
        Produtos newProduct = product(null, "SKU-1");
        when(produtosRepository.existsByCodigo("SKU-1")).thenReturn(true);

        assertThatThrownBy(() -> service.save(newProduct))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Já existe um produto cadastrado com o código informado");
        verify(produtosRepository, never()).save(any());
    }

    @Test
    void shouldRejectInvalidCodeFromProductOnSave() {
        Produtos invalid = mock(Produtos.class);
        when(invalid.codigo()).thenReturn(" ");

        assertThatIllegalArgumentException().isThrownBy(() -> service.save(invalid))
                .withMessage("código não pode ser vazio");
        verifyNoInteractions(produtosRepository);
    }

    @Test
    void shouldUpdateProductAndKeepItsId() {
        Produtos existing = product("1", "SKU-1");
        Produtos changes = new Produtos(null, "SKU-2", "Updated", "New description", 20.0);
        Produtos saved = new Produtos("1", "SKU-2", "Updated", "New description", 20.0);
        when(produtosRepository.findById("1")).thenReturn(Optional.of(existing));
        when(produtosRepository.findByCodigo("SKU-2")).thenReturn(Optional.empty());
        when(produtosRepository.save(saved)).thenReturn(saved);

        assertThat(service.update("1", changes)).isEqualTo(saved);
        verify(produtosRepository).save(saved);
    }

    @Test
    void shouldAllowUpdatingWithTheSameCode() {
        Produtos existing = product("1", "SKU-1");
        when(produtosRepository.findById("1")).thenReturn(Optional.of(existing));
        when(produtosRepository.findByCodigo("SKU-1")).thenReturn(Optional.of(existing));
        when(produtosRepository.save(any(Produtos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.update("1", product(null, "SKU-1")))
                .isEqualTo(product("1", "SKU-1"));
    }

    @Test
    void shouldRejectDuplicatedCodeOnUpdate() {
        Produtos existing = product("1", "SKU-1");
        Produtos another = product("2", "SKU-2");
        when(produtosRepository.findById("1")).thenReturn(Optional.of(existing));
        when(produtosRepository.findByCodigo("SKU-2")).thenReturn(Optional.of(another));

        assertThatThrownBy(() -> service.update("1", product(null, "SKU-2")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(produtosRepository, never()).save(any());
    }

    @Test
    void shouldRejectNullProductOnUpdate() {
        assertThatNullPointerException().isThrownBy(() -> service.update("1", null))
                .withMessage("produto não pode ser nulo");
        verifyNoInteractions(produtosRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectInvalidIdOnUpdateAndDelete(String id) {
        assertThatThrownBy(() -> service.update(id, product(null, "SKU-1")))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
        verifyNoInteractions(produtosRepository);
    }

    @Test
    void shouldDeleteProductWhenItExists() {
        when(produtosRepository.findById("1")).thenReturn(Optional.of(product("1", "SKU-1")));

        service.deleteById("1");

        verify(produtosRepository).deleteById("1");
    }

    @Test
    void shouldNotDeleteMissingProduct() {
        when(produtosRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(produtosRepository, never()).deleteById(anyString());
    }

    @Test
    void shouldRejectCodeWhenTheCurrentIdIsNullInAvailabilityCheck() throws Exception {
        when(produtosRepository.findByCodigo("SKU-1")).thenReturn(Optional.of(product("1", "SKU-1")));
        Method method = ProdutosServiceImpl.class.getDeclaredMethod("ensureCodigoIsAvailable", String.class, String.class);
        method.setAccessible(true);

        assertThatThrownBy(() -> method.invoke(service, "SKU-1", null))
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(DuplicateResourceException.class);
    }

    private static Produtos product(String id, String code) {
        return new Produtos(id, code, "Product", "Description", 10.0);
    }
}
