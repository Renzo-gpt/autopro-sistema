package com.utp.autopro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El DNI o RUC es obligatorio")
    @Size(min = 8, max = 11, message = "El documento debe tener entre 8 y 11 dígitos")
    @Column(nullable = false, unique = true, length = 15)
    private String documento;

    @NotBlank(message = "El nombre completo o razón social es obligatorio")
    @Column(nullable = false, length = 120)
    private String nombres;

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String direccion;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Vehiculo> vehiculos = new ArrayList<>();
}
