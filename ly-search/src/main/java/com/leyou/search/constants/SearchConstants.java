package com.leyou.search.constants;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/17 10:54
 */
public class SearchConstants {

    public static final String SEARCH_FIELD = "title";
    public static final String HIGHLIGHT_FIELD = "title";
    public static final String[] INCLUDE_SOURCE = new String[]{"id","title","prices","sold","image"};
    public static final String[] EMPTY_SOURCE = new String[0];
    public static final String PRE_TAG = "<am>";
    public static final String POST_TAG = "</am>";




    public static final String SUGGEST_FIELD = "suggestion";

    public static final String SOURCE_TEMPLATE ="{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": {\n" +
            "        \"my_pinyin\": {\n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\n" +
            "            \"py\"\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"py\": {\n" +
            "\t\t  \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"suggestion\": {\n" +
            "        \"type\": \"completion\",\n" +
            "        \"analyzer\": \"my_pinyin\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"title\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"my_pinyin\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"updateTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      },\n" +
            "      \"specs\":{\n" +
            "        \"type\": \"nested\",\n" +
            "        \"properties\": {\n" +
            "          \"name\":{\"type\": \"keyword\" },\n" +
            "          \"value\":{\"type\": \"keyword\" }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

}
