import React, {useEffect, useState} from "react";
import axios from "axios";
import {
    Typography,
    Button,
    Snackbar, Grid2, Card, CardContent, Alert,
} from "@mui/material";
import style from "../../styles/quotationRequest-list.module.scss";
import Header from "../../components/common/header.jsx";
import Footer from "../../components/common/footer.jsx";
import {useNavigate, useParams} from "react-router-dom";
import {CheckCircle, Pending, Person} from "@mui/icons-material";
import Avatar from "@mui/material/Avatar";

const QuotationRequestList = () => {

    const path = window.location.pathname;
    const navigate = useNavigate();

    const [quotationRequests, setQuotationRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [pageInfo, setPageInfo] = useState({
        progress: "PENDING", // PENDING, APPROVED, ALL
        page: 0,
        totalPage: 0,
        size: 6,
    });
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");

    useEffect(() => {
        const fetchQuotationRequests = async () => {
            setLoading(true);
            setError(null);

            let url;
            if (path.endsWith("member")) {
                url = `/api/quotationRequest/list?progress=${pageInfo.progress}&page=${pageInfo.page}&pageSize=${pageInfo.size}`;
            } else if (path.endsWith("company")) {
                url = `/api/quotationRequest/companyList?progress=${pageInfo.progress}&page=${pageInfo.page}&pageSize=${pageInfo.size}`;
            }

            try {
                const response = await axios.get(url);
                console.log(response.data);
                (pageInfo.page === 0)
                    ? setQuotationRequests(response.data.content)
                    : setQuotationRequests((prevRequests) => [
                        ...prevRequests,
                        ...response.data.content,
                    ]);
                setPageInfo({...pageInfo, totalPage: response.data.page.totalPages});

            } catch (err) {
                console.error(err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchQuotationRequests();
    }, [pageInfo.page, pageInfo.progress]);

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const updateProgress = async (id) => {
        if (!confirm("해당 거래를 취소하시겠습니까?")) return;

        try {
            await axios.put(`/api/quotationRequest/sellerCancel/${id}`);
            setSnackbarMessage("진행 상태가 업데이트되었습니다.");
            setSnackbarOpen(true);
            setPageInfo({...pageInfo, progress: "ALL", page: 0})
        } catch (err) {
            console.error(err);
            setSnackbarMessage("진행 상태 업데이트 실패.");
            setSnackbarOpen(true);
        }
    };

    const setProgress = (progress) => {
        if (pageInfo.progress !== progress) {
            setPageInfo({...pageInfo, progress: progress, page: 0});
        }
    }

    const progressIcon = (progress) => {
        if (progress === "PENDING") {
            return <Pending/>;
        } else if (progress === "APPROVED") {
            return <CheckCircle/>;
        } else if (progress === "USER_CANCELLED") {
            return "유저 취소";
        } else if (progress === "SELLER_CANCELLED") {
            return "판매자 취소";
        } else if (progress === "ADMIN_CANCELLED") {
            return "관리자 취소";
        } else {
            return "진행 상태";
        }
    }

    if (error) {
        alert("오류가 발생했습니다.");
        navigate(-1);
    }

    return (
        <>
            <Header/>
            <main className={style['main']}>
                <div className={style['container']}>
                    <Typography variant="h6" style={{textAlign: "center", margin: "24px 0"}}>
                        회사 견적신청서 목록
                    </Typography>
                    <Grid2 container spacing={2} className={style['qr-grid-container']}>
                        <Grid2 size={2} className={style['qr-grid']}>
                            <Button variant="outlined"
                                    className={style[(pageInfo.progress === "PENDING" ? "focus-button" : "button")]}
                                    onClick={() => setProgress("PENDING")}>
                                진행중인 목록
                            </Button>
                        </Grid2>
                        <Grid2 size={2} className={style['qr-grid']}>
                            <Button variant="outlined"
                                    className={style[(pageInfo.progress === "APPROVED" ? "focus-button" : "button")]}
                                    onClick={() => setProgress("APPROVED")}>
                                완료된 목록
                            </Button>
                        </Grid2>
                        <Grid2 size={2} className={style['qr-grid']}>
                            <Button variant="outlined"
                                    className={style[(pageInfo.progress === "ALL" ? "focus-button" : "button")]}
                                    onClick={() => setProgress("ALL")}>
                                전체 목록
                            </Button>
                        </Grid2>
                    </Grid2>
                    <Grid2 container spacing={2} className={style['qr-card-container']}>
                        {quotationRequests.map(request =>
                            <Grid2 key={request.id} size={6} className={style['qr-card-grid']}>
                                <Card variant="outlined" className={style['qr-card']}>
                                    <CardContent className={style['qr-card-content']}>
                                        <div className={style['image-div']}>
                                            {path.endsWith("company") &&
                                                <div className={style['member-div']}>
                                                    {(request.member.memberUrl)
                                                        ? <Avatar alt="member profile"
                                                                  src={request.member.memberUrl}/>
                                                        : <Avatar alt="member profile">
                                                            <Person/>
                                                        </Avatar>}
                                                    <div>{request.member.username}</div>
                                                </div>
                                            }
                                            <Typography variant="subtitle1" className={style[request.progress]}>
                                                {progressIcon(request.progress)}
                                            </Typography>
                                        </div>
                                        <div className={style['info-div']}>
                                            <Typography variant="subtitle2">{request.title}</Typography>
                                            <Typography variant="body2">{request.description}</Typography>
                                        </div>
                                        <div className={style['bottom-div']}>
                                            <div className={style['date-div']}>
                                                {request.updatedAt}
                                            </div>
                                            <div className={style['button-div']}>
                                                {request.progress === "PENDING" &&
                                                    <Button className={style['button']}
                                                            onClick={() => updateProgress(request.id)}>
                                                        거래 취소
                                                    </Button>}
                                                <Button className={style['button']}
                                                        onClick={() => navigate(`/quotationRequest/${request.id}`)}>
                                                    상세 정보
                                                </Button>
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            </Grid2>
                        )}
                    </Grid2>

                    {(pageInfo.page + 1 < pageInfo.totalPage) && (
                        <Button onClick={() => setPageInfo({...pageInfo, page: pageInfo.page + 1})}
                                disabled={loading}>
                            {loading ? "Loading..." : "더보기"}
                        </Button>
                    )}
                    <Snackbar
                        anchorOrigin={{vertical: "top", horizontal: "center"}}
                        open={snackbarOpen}
                        autoHideDuration={3000}
                        onClose={handleSnackbarClose}
                        message={snackbarMessage}
                        sx={{marginTop: "40px"}}>
                        <Alert
                            severity="success"
                            variant="outlined"
                            sx={{
                                width: '100%',
                                bgcolor: 'background.paper',
                            }}>
                            {snackbarMessage}
                        </Alert>
                    </Snackbar>
                </div>
            </main>
            <Footer/>
        </>
    );
};

export default QuotationRequestList;

