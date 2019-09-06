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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "backReferenceId")
@NamedEntityGraph(
        name = "integration.expanded",
        attributeNodes = {
                @NamedAttributeNode(value = "type", subgraph ="type-subgraph"),
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
    private IntegrationType type;

    @OneToMany(mappedBy = "integration")
    private List<IntegrationSetting> settings;

    public Optional<String> getAttributeValue(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        String value = integrationSetting == null ? null : integrationSetting.getValue();
        return Optional.ofNullable(value);
    }

    public Optional<byte[]> getAttributeBinaryData(String attributeName) {
        IntegrationSetting integrationSetting = getAttribute(attributeName)
                .orElse(null);
        byte[] binaryData = integrationSetting == null ? null : integrationSetting.getBinaryData();
        return Optional.ofNullable(binaryData);
    }

    private Optional<IntegrationSetting> getAttribute(String attributeName) {
        return this.getSettings().stream()
                   .filter(is -> is.getParam().getName().equals(attributeName))
                   .findAny();
    }
}
