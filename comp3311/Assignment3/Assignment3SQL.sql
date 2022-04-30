-- 1
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

-- 2
SELECT
    username, name
FROM
    account
WHERE
    registrationdate BETWEEN '2014-01-01' AND '2016-01-01';
    
-- 3

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
                        c.castid = a.castid AND c.castname = 'Robert Downey Jr.'
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
                            AND c.castname = 'Robert Downey Jr.'
                    ) v, watches
                WHERE
                    v.videoid = watches.videoid
                GROUP BY
                    v.videoid
            )
    );

--4

SELECT
    CASTID, CASTNAME 
FROM
    castmember
WHERE
    castid IN (
        SELECT DISTINCT
            castid
        FROM
            acts  NATURAL JOIN tvshow
        MINUS
        SELECT DISTINCT
            castid
        FROM
                 acts
            NATURAL JOIN movie
    );
    
--5
SELECT
    title
FROM
    tvshow NATURAL JOIN video
GROUP BY
    title
HAVING
    SUM(runtime) = (
        SELECT
            MAX(SUM(runtime))
        FROM
            tvshow NATURAL JOIN video
        GROUP BY
            title
    );

--6

SELECT
    *
FROM
    account
where 
    round((to_date('2019-09-01', 'YYYY-MM-DD') - birthdate)/365, 1) > 21;


--7
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
          
                 
--8
/**
select 
    trim(upper(sequel))
from 
    movie natural join video
where 
    upper(trim(title)) in (
                SELECT
                    upper(trim(regexp_substr(sequel, '[^,]+', 1, 2))) AS title
                FROM
                    movie
                WHERE
                    sequel LIKE '%,%'
            )
union
**/

SELECT
   regexp_substr(sequel, '[^,]+', 1, 2) AS title
FROM
    movie
WHERE
    sequel LIKE '%,%';
--9

SELECT DISTINCT
    playlistname, username
FROM
    playlist
WHERE
    videoid NOT IN (
        SELECT
            p.videoid
        FROM
            account  a, playlist p, watches  w
        WHERE
                a.username = p.username
            AND p.videoid = w.videoid
            AND a.username = w.username
    );
                       
--10
SELECT
    a.username
FROM
    account a,
    watches w,
    (
        SELECT DISTINCT
            videoid
        FROM
                 acts
            NATURAL JOIN castmember
        WHERE
            castname = 'Chris Evans'
    )       ce
WHERE
        a.username = w.username
    AND w.videoid = ce.videoid
GROUP BY
    a.username
HAVING
    COUNT(*) = (
        SELECT
            COUNT(videoid)
        FROM
                 acts
            NATURAL JOIN castmember
        WHERE
            castname = 'Chris Evans'
    );