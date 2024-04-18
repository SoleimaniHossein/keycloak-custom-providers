package soleimanihossein.keycloak.protocol.groupmapper;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import soleimanihossein.keycloak.protocol.groupmapper.entity.CustomGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author soleimaniHossein
 */

public class CustomGroupMapper extends AbstractOIDCProtocolMapper
		implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

	public static final String PROVIDER_ID = "oidc-custom-group-mapper";

	private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

	static final String ATTR_KEY = "key";
	static final String ATTR_VALUES = "values";

	static {

		configProperties.add(new ProviderConfigProperty(ATTR_KEY, "Attribute key", "Group Attribute Key.",
				ProviderConfigProperty.STRING_TYPE, null));

		configProperties.add(new ProviderConfigProperty(ATTR_VALUES, "Attribute values", "Group Attribute Values.",
				ProviderConfigProperty.MULTIVALUED_STRING_TYPE, null));

		OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
		OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, CustomGroupMapper.class);
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getDisplayCategory() {
		return TOKEN_MAPPER_CATEGORY;
	}

	@Override
	public String getDisplayType() {
		return "Group Membership Filter";
	}

	@Override
	public String getHelpText() {
		return "Map user group membership by group Attribute.";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return configProperties;
	}

	@Override
	protected void setClaim(final IDToken token, final ProtocolMapperModel mappingModel,
			final UserSessionModel userSession,
			final KeycloakSession keycloakSession, final ClientSessionContext clientSessionCtx) {

		final String attrKey = mappingModel.getConfig().get(ATTR_KEY);
		final String attrValue = mappingModel.getConfig().get(ATTR_VALUES);

		if (attrKey == null || attrValue == null) {
			return;
		}

		// Set the MULTIVALUED conig
		mappingModel.getConfig().put(ProtocolMapperUtils.MULTIVALUED, Boolean.TRUE.toString());

		var groups = userSession.getUser().getGroupsStream().map(group -> {
			var attributes = group.getAttributes();

			if (attributes != null && attributes.containsKey(attrKey)) {
				List<String> attrValues = new ArrayList<String>(Arrays.asList(attrValue.split("##")));

				for (String value : attrValues) {
					if (attributes.get(attrKey).contains(value)) {
						return new CustomGroup(group.getId(), group.getName());
					}
				}
				return null;

			} else {
				return null;
			}
		}).filter(data -> data != null);
		OIDCAttributeMapperHelper.mapClaim(token, mappingModel, groups);
	}

}
