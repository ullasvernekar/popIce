package com.ot.popIce.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BillProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double quantity;

    private double totalPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    @JsonBackReference("popBill")
    private Bill bill;
}
