package com.nosugarice.mybatis.test.query;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/7
 */
@Table(name = "film_review")
public class FilmReview implements Serializable {

    private static final long serialVersionUID = -8793166370987026047L;

    /** 主键 */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 用户名称 */
    @Column(name = "user_name", nullable = false)
    private String userName;

    /** 用户性别 */
    @Column(name = "sex", nullable = false)
    private String sex;

    /** 映射名称 */
    @Column(name = "movie_name", nullable = false)
    private String movieName;

    /** 评分 */
    @Column(name = "score", nullable = false)
    private Integer score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
