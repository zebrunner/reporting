/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.services.services.application.jmx;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.util.ElasticsearchResultHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
@Service
public class ElasticsearchService implements IJMXService
{

	private static final Logger LOGGER = Logger.getLogger(ElasticsearchService.class);

	@Value("${zafira.elasticsearch.url}")
	private String url;

	@Value("${zafira.elasticsearch.user}")
	private String user;

	@Value("${zafira.elasticsearch.pass}")
	private String password;

	private RestHighLevelClient client;

	@PostConstruct
	public void initInstance() {
		RestClientBuilder builder = getBuilder(url);
		if(!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
			builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
		}
		this.client = new RestHighLevelClient(builder);
	}

	public Map<String, String> getScreenshotsInfo(String correlationId, String... indices) {
		Map<String, String> result = new HashMap<>();
		try {
			SearchResponse response = search(SearchBuilder.ALL, prepareCorrelationIdMap(correlationId), indices);
			String lastMessage = null;
			for(SearchHit hit : response.getHits().getHits()) {
				if(ElasticsearchResultHelper.getMessage(hit) != null && ElasticsearchResultHelper.getHeaders(hit) == null) {
					lastMessage = ElasticsearchResultHelper.getMessage(hit);
				}
				if(ElasticsearchResultHelper.getHeaders(hit) != null && ElasticsearchResultHelper.getAmazonPath(hit) != null) {
					result.put(ElasticsearchResultHelper.getAmazonPath(hit), lastMessage);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot get screenshots from elasticsearch", e);
		}
		return result;
	}

	public List<String> getScreenshots(String correlationId, String... indices) {
		List<String> result;
		try {
			SearchResponse response = search(SearchBuilder.SCREENSHOTS, prepareCorrelationIdMap(correlationId), indices);
			result = Arrays.stream(response.getHits().getHits()).map(ElasticsearchResultHelper::getAmazonPath).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException("Cannot get screenshots from elasticsearch", e);
		}
		return result;
	}

	public List<String> getMessages(String correlationId, String... indices) {
		List<String> result;
		try {
			SearchResponse response = search(SearchBuilder.MESSAGES, prepareCorrelationIdMap(correlationId), indices);
			result = Arrays.stream(response.getHits().getHits()).map(ElasticsearchResultHelper::getMessage).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException("Cannot get screenshots from elasticsearch", e);
		}
		return result;
	}

	public SearchResponse search(SearchBuilder searchBuilder, Map<String, String> map, String... indices) throws IOException {
		return search(searchBuilder.apply(map), indices);
	}

	public SearchResponse search(QueryBuilder queryBuilder, String... indices) throws IOException {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(queryBuilder);
		SearchRequest request = new SearchRequest();
		request.source(searchSourceBuilder).indices(indices);
		return client.search(request, RequestOptions.DEFAULT);
	}

	public enum SearchBuilder {

		ALL(map -> {
			return QueryBuilders.boolQuery()
								.must(QueryBuilders.termQuery("correlation-id", map.get("correlationId")));
		}),
		SCREENSHOTS(map -> {
			return QueryBuilders.boolQuery()
						 .must(QueryBuilders.termQuery("correlation-id", map.get("correlationId")))
								.must(QueryBuilders.existsQuery("headers.AMAZON_PATH"));
		}),
		MESSAGES(map -> {
			return QueryBuilders.boolQuery()
								.must(QueryBuilders.termQuery("correlation-id", map.get("correlationId")))
								.mustNot(QueryBuilders.existsQuery("headers"));
		});

		private Function<Map<String, String>, QueryBuilder> builder;

		SearchBuilder(Function<Map<String, String>, QueryBuilder> builder) {
			this.builder = builder;
		}

		public QueryBuilder apply(Map<String, String> map) {
			return this.builder.apply(map);
		}
	}

	@Override
	public void init()
	{
	}

	@Override
	public boolean isConnected()
	{
		return true;
	}

	private static RestClientBuilder getBuilder(String path) {
		String prefix = null;
		HttpHost host = null;
		try {
			URL url = new URL(path);
			host = HttpHost.create(url.getHost());
			prefix = url.getPath();
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return RestClient.builder(host).setPathPrefix(prefix);
	}

	private static Map<String, String> prepareCorrelationIdMap(String correlationId) {
		return new HashMap<String, String>() {
			{
				put("correlationId", correlationId);
			}
		};
	}

	public List<Setting> getSettings()
	{
		return new ArrayList<Setting>()
		{
			private static final long serialVersionUID = 7140283430898343120L;
			{
				add(new Setting()
				{
					private static final long serialVersionUID = 658548604106441383L;
					{
						setName("URL");
						setValue(url);
					}
				});
				add(new Setting()
				{
					private static final long serialVersionUID = 6585486043214259383L;
					{
						setName("user");
						setValue(user);
					}
				});
				add(new Setting()
				{
					private static final long serialVersionUID = 6585486425564259383L;
					{
						setName("password");
						setValue(password);
					}
				});
			}
		};
	}
}
