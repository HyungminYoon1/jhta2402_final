import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Typography, List, ListItem, ListItemText, CircularProgress, Button, Grid } from '@mui/material';
import { useLocation } from 'react-router-dom';
import Header from '../components/common/header';

function SearchList() {
    const location = useLocation();
    const query = new URLSearchParams(location.search).get('query');
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    useEffect(() => {
        const fetchSearchResults = async () => {
            setLoading(true);
            setError(null);

            try {
                const response = await axios.get(`/api/portfolio/search/detailed?searchWord=${query}&page=${page}&size=10`);
                setResults(prevResults => [...prevResults, ...response.data.content]);
                setHasMore(response.data.content.length > 0); // 다음 페이지가 있는지 확인
            } catch (err) {
                setError('검색 결과를 가져오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        if (query) {
            fetchSearchResults();
        }
    }, [query, page]);

    const loadMoreResults = () => {
        if (hasMore) {
            setPage(prevPage => prevPage + 1);
        }
    };

    if (loading && page === 0) {
        return <CircularProgress />;
    }
    if (error) {
        return <Typography color="error">{error}</Typography>;
    }

    return (
        <div>
            <Header />
            <Typography variant="h6" style={{ marginTop: '80px', marginLeft: '30px' }}>" {query} "의 검색 결과</Typography>
            <Grid >
                {results.length > 0 ? (
                    results.map((item, index) => (
                        <ListItem key={index} divider>
                            <ListItemText
                                primary={item.title}
                                secondary={
                                    <>
                                        <span style={{ color: 'black' }}>{item.description}</span>
                                        <br />
                                        <span style={{ color: 'gray' }}>{item.companyName}</span>
                                    </>
                                }
                            />
                            <Grid container spacing={1}>
                                {item.imageUrls && item.imageUrls.map((url, imgIndex) => (
                                    <Grid item key={imgIndex} style={{ marginLeft: '8px' }}>
                                        <img src={`https://picsum.photos/seed/${imgIndex}/600/400`} alt={`image-${imgIndex}`} style={{ width: '100px', height: 'auto', borderRadius: '4px' }} />
                                    </Grid>
                                ))}
                            </Grid>
                        </ListItem>
                    ))
                ) : (
                    <Typography>No results found</Typography>
                )}
            </Grid>
            {hasMore && (
                <Button variant="contained" color="primary" onClick={loadMoreResults} disabled={loading}>
                    {loading ? 'Loading...' : '더보기'}
                </Button>
            )}
        </div>
    );
}

export default SearchList;
