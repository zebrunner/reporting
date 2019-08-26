package com.qaprosoft.zafira.models.entity.integration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "backReferenceId")
@NamedEntityGraph(
    name = "integration.expanded",
    attributeNodes = {
        @NamedAttributeNode("type"),
        @NamedAttributeNode(value = "settings", subgraph = "settings-subgraph")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "settings-subgraph",
            attributeNodes = {
                @NamedAttributeNode("param")
            }
        )
    }
)
@Entity
@Table(name = "integrations")
public class Integration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String backReferenceId;
    private boolean isDefault;
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "integration_type_id")
    IntegrationType type;

    @OneToMany(mappedBy = "integration")
    List<IntegrationSetting> settings;

}
