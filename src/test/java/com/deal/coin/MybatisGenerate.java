package com.deal.coin;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.PostgreSqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 生成代码前首先配置：
 *  代码路径配置（生成文件保存路径）
 *  生成内容配置（需要生成的数据库表）
 */
public class MybatisGenerate {


    //代码作者
    final static String AUTH = "caic";
    //项目代码地址
    final static String BasePath = "/Users/caichuang/Tepulo/coin/src/main/";
    //java代码根路径
    final static String PATH = BasePath + "java" + File.separator;
    //项目资源文件根路径
    final static String PATH_MAPPER = BasePath + "resources" + File.separator;
    //项目包根路径
    final static String PACKAGEPARENT = "com.deal.coin";
    final static String PACKAGEPARENT_PATH = PACKAGEPARENT.replaceAll("\\.", "\\" + File.separator) + File.separator;

    //schema
    final static String TABLE_SCHEMA_NAME = "coin";
    //表名，多个英文逗号分割
    final static String TABLES_NAME = "t_buy_record";


    // 数据库配置
    final static String DB_DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    final static String DB_USER_NAME = "tupelo";
    final static String DB_PASSWORD = "940915";
    final static String DB_URL = "jdbc:mysql://106.52.145.64:3306/coin?useUnicode=true&characterEncoding=UTF-8";


    final static String ENTITY_PATH = PATH + PACKAGEPARENT_PATH + "entity" + File.separator;
    final static String MAPPER_PATH = PATH_MAPPER + "mapper" + File.separator;
    final static String SERVICE_PATH = PATH + PACKAGEPARENT_PATH + "service" + File.separator;
    final static String SERVICE_IMPL_PATH = PATH + PACKAGEPARENT_PATH + "service" + File.separator + "impl" + File.separator;


    public static void main(String[] args) {

        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(PATH);					//输出目录
        gc.setFileOverride(false);				//是否覆盖
        gc.setActiveRecord(true);
        gc.setEnableCache(false);				// XML 二级缓存
        gc.setBaseResultMap(true);				// XML ResultMap
        gc.setBaseColumnList(true);				// XML columList
        gc.setOpen(false);						//生成后打开文件夹
        gc.setAuthor(AUTH);
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");

        mpg.setGlobalConfig(gc);

        // 数据库配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new PostgreSqlTypeConvert());
        dsc.setDriverName(DB_DRIVER_NAME);
        dsc.setUsername(DB_USER_NAME);
        dsc.setPassword(DB_PASSWORD);
        dsc.setUrl(DB_URL);
        dsc.setSchemaName(TABLE_SCHEMA_NAME);
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(TABLES_NAME.split(","));
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 自定义实体，公共字段
        strategy.setSuperEntityColumns(new String[] {});
        // 自定义 mapper 父类
//		strategy.setSuperMapperClass("com.manager.data.dao.mybatis.base.BaseDao");
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pkg = new PackageConfig();
        pkg.setParent(PACKAGEPARENT);
        pkg.setMapper("mapper");
        pkg.setService("service");
        pkg.setEntity("entity");
        pkg.setServiceImpl("service.impl");
        mpg.setPackageInfo(pkg);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 【可无】
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("schema",TABLE_SCHEMA_NAME);
                this.setMap(map);
            }
        };
        // 自定义 xxList.jsp 生成
        List<FileOutConfig> focList = new ArrayList<FileOutConfig>();

        // 调整 xml 生成目录
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return MAPPER_PATH + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        focList.add(new FileOutConfig("/templates/service.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return SERVICE_PATH
                        + "I" + tableInfo.getEntityName() + "Service.java";
            }
        });
        focList.add(new FileOutConfig("/templates/serviceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return SERVICE_IMPL_PATH
                        + tableInfo.getEntityName() + "ServiceImpl.java";
            }
        });
        focList.add(new FileOutConfig("/templates/entity.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return ENTITY_PATH
                        + tableInfo.getEntityName() + ".java";
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        TemplateConfig tc = new TemplateConfig();
        tc.setXml(null);
        tc.setController(null);
        tc.setService(null);
        tc.setServiceImpl(null);
        tc.setEntity(null);
        mpg.setTemplate(tc);

        // 执行生成
        mpg.execute();
    }
}