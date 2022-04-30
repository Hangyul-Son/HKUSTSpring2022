--Q1
SELECT 
    title
FROM
    video natural join movie
WHERE
    videoid IN (
        SELECT distinct
            videoid
        FROM
            tags
        WHERE
            tag = 'Comedy'
    );

--Q2
SELECT
    username, name
FROM
    account
WHERE
    registrationdate BETWEEN 'date1' AND 'date2';

--Q3
SELECT
    videoid, title
FROM
    video
WHERE
    videoid IN (
        SELECT distinct
            v.videoid
        FROM
            (
                SELECT
                    videoid
                FROM
                    castmember c, acts a
                WHERE
                        c.castid = a.castid AND c.castname = 'actor'
            ) v, watches
        WHERE
            v.videoid = watches.videoid
        GROUP BY
            v.videoid
        HAVING
            COUNT(*) = (
                SELECT
                    MAX(COUNT(*))
                FROM
                    (
                        SELECT
                            videoid
                        FROM
                            castmember c, acts a
                        WHERE
                                c.castid = a.castid
                            AND c.castname = 'actor'
                    ) v, watches
                WHERE
                    v.videoid = watches.videoid
                GROUP BY
                    v.videoid
            )
    );
    
--Q4 
SELECT
    *
FROM
    account
where 
    round((to_date('2022-05-01', 'YYYY-MM-DD') - birthdate)/365, 1) > age;

--Q5
SELECT
    username, playlistname
FROM
    playlist
minus
SELECT 
    username, playlistname
FROM
    playlist
WHERE
    videoid IN (
        SELECT
            videoid
        FROM
            movie
        WHERE
            to_date(concat(to_char(dateofrelease),'-01-01'), 'yyyy-mm-dd') >= to_date('year' + '-01-01', 'yyyy-mm-dd')
        UNION
        SELECT
            videoid
        FROM
            tvshow
        WHERE
            to_date(concat(to_char(dateofrelease),'-01-01'), 'yyyy-mm-dd') >= to_date('year' + '-01-01', 'yyyy-mm-dd')
    );

--Q6
INSERT INTO account values('username', 'name', 'email', 'birthdate', to_date(sysdate, 'yyyy-mm-dd'));
INSERT INTO premium values('username');

--Q7
delete from account where username = 'username';



